import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Cats And Food
 *
 * @author DSerov
 * @version dated 17 feb, 2018
 * @link https://github.com/dserov/CatsAndFood
 */
public class GameField extends JPanel implements Runnable {
    private final int W_HEIGHT = 300;
    private final int W_WIDTH = 600;
    private final long DELAY = 20; // умолчательное значение задержки следующего рабочего цикла
    private Thread runner;
    private List<Entity> entities = new ArrayList<>();


    final private int CATS_COUNT = 5;
    private Cat[] cats = new Cat[CATS_COUNT];
    private Plate plate;

    private Cat catCurrent;
    private int catNum = -1;
    boolean allCatsNotHungry = true; // признак, что все коты накормлены

    enum Stage {
        MOVE_PLATE,
        CAT_EATING,
        SELECT_NEXT_CAT,
        PLATE_IS_EMPTY,
        IDLE
    }

    private Stage stage;

    // служит для пересчета состояния сущностей приложения
    private void update(long timeDelay) {
        System.out.println("Current stage = " + stage);
        // select new stage

        switch (stage) {
            case IDLE:
                if (catCurrent == null)
                    stage = Stage.SELECT_NEXT_CAT;
                else {
                    if (!plate.isPlateEmpty())
                        stage = Stage.SELECT_NEXT_CAT;
                }
                break;
            case SELECT_NEXT_CAT:
                // выбор следующего голодного кота
                catCurrent = getNextHungryCat();
                if (catCurrent == null || plate.isPlateEmpty()) {
                    // все коты накормлены, тарелку на базу. или тарелка пуста
                    plate.moveTo(0, 150);
                } else {
                    // к нему поехала тарелочка
                    plate.moveTo((int) catCurrent.getX(), (int) catCurrent.getMaxY());
                }
                stage = Stage.MOVE_PLATE;
                break;
            case MOVE_PLATE:
                if (!plate.isBusy()) {
                    if (catCurrent == null) {
                        stage = Stage.PLATE_IS_EMPTY; // тарелка приехала на базу
                    }
                    else {
                        catCurrent.eat(plate);
                        stage = Stage.CAT_EATING; // пока не доедет, не переключаем
                    }
                }
                break;
            case CAT_EATING:
                if (!catCurrent.isBusy())
                    stage = Stage.IDLE;
                break;
            case PLATE_IS_EMPTY:
                if (!plate.isPlateEmpty())
                    stage = Stage.IDLE;
                break;
        }

        for (Entity entity : entities)
            entity.update(timeDelay);
    }

    private Cat getNextHungryCat() {
        Cat cat = null;
        allCatsNotHungry = true;
        for (int i = 0; i < cats.length; i++) {
            catNum++;
            if (catNum >= cats.length) catNum = 0;
            cat = cats[catNum];
            allCatsNotHungry &= !cat.isHungry();
            if (!allCatsNotHungry) break; // найден голодный котик
        }
        return (allCatsNotHungry) ? null : cat;
    }

    GameField() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(W_WIDTH, W_HEIGHT));
        setDoubleBuffered(true);

        init();
        stage = Stage.IDLE;
    }

    // вызывается при добавлении панельки к фрейму
    @Override
    public void addNotify() {
        super.addNotify();

        runner = new Thread(this);
        runner.start();
    }

    // вызывается для перерисовки компонента
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // рисуем все сущности
        for (Entity entity : entities)
            entity.render(g);
    }


    // тело потока
    @Override
    public void run() {
        long lastFrameTime = System.currentTimeMillis(), sleepTime, fps = 0, frameTime = 0;

        Thread thisThread = Thread.currentThread();
        while (runner == thisThread) {
            // расчитаем и нормализуем время задержки в ms
            sleepTime = DELAY - (System.currentTimeMillis() - lastFrameTime);
            sleepTime = Math.max(2, sleepTime);

            // пересчитаем все сущности
            update(sleepTime);
            // скомандуем яве перерисовать компонент
            repaint();

            // calc the fps count
            fps++;
            frameTime += sleepTime;
            if (frameTime > 1000) {
                fps = 0;
                frameTime = 0;
            }

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                System.out.println("Interrupted: " + e.getMessage());
            }
            lastFrameTime = System.currentTimeMillis();
        }


        // инфа о тарелке
        plate.info();
        // кормим котов, пока тарелка не опустеет или все не накормятся
        while (true) {
            allCatsNotHungry = true;
            for (Cat cat : cats) {
                cat.eat(plate); // кот лизнул из тарелки
                allCatsNotHungry &= !cat.isHungry(); // если хоть один кот голоден, скинется в false
            }
            if (allCatsNotHungry || plate.isPlateEmpty())
                break; // накормлены все, либо тарелка пуста
        }
        // вывод инфы
        for (Cat cat : cats)
            System.out.println(cat); // статус кота
        plate.info(); // статус тарелки
    }

    // создаем объекты приложения
    private void init() {
        // создаем котов и тарелку
        // Создаем котов с разным аппетитом от 5 до 25
        for (int i = 0; i < CATS_COUNT; i++) {
            cats[i] = new Cat("Kot-" + i);
            entities.add(cats[i]);

            cats[i].setLocation(100 * i + 30, 10);
        }
        plate = new Plate();
        plate.setLocation(0, 150);
        entities.add(plate);
    }
}
