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

    GameField() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(W_WIDTH, W_HEIGHT));
        setDoubleBuffered(true);

//        loadImage();
        init();
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
        for (Entity entity: entities)
            entity.render(g);
    }

    // служит для пересчета состояния сущностей приложения
    private void update(long timeDelay) {
        for (Entity entity: entities)
            entity.update(timeDelay);
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
//                System.out.println("fps: " + fps);
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
        boolean allCatsNotHungry; // признак, что все коты накормлены
        while (true) {
            allCatsNotHungry = true;
            for (Cat cat : cats) {
                cat.eat(plate); // кот лизнул из тарелки
                allCatsNotHungry &= !cat.isHangry(); // если хоть один кот голоден, скинется в false
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
        }
        plate = new Plate();
        entities.add(plate);
    }
}
