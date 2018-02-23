import com.sun.corba.se.spi.legacy.interceptor.ORBInitInfoExt;
import sun.audio.*;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

/**
 * Cats And Food
 *
 * @author DSerov
 * @version dated 17 feb, 2018
 * @link https://github.com/dserov/CatsAndFood
 */
public class GameField extends JPanel implements Runnable {
    private final int W_HEIGHT = 520;
    private final int W_WIDTH = 600;
    private final long DELAY = 20; // умолчательное значение задержки следующего рабочего цикла
    private Thread runner;

    final private int CATS_COUNT = 3;
    private Cat[] cats = new Cat[CATS_COUNT];
    private Plate plate;

    private Cat catCurrent;
    private int catNum = -1;

    enum Stage {
        MOVE_PLATE,
        CAT_EATING,
        SELECT_NEXT_CAT,
        PLATE_IS_EMPTY,
        IDLE,
    }

    private Stage stage;

    // служит для пересчета состояния сущностей приложения
    private void update(long timeDelay) {
        switch (stage) {
            case IDLE:
                if (catCurrent == null && !plate.isPlateEmpty())
                    stage = Stage.SELECT_NEXT_CAT;
                else {
                    if (!plate.isPlateEmpty())
                        stage = Stage.SELECT_NEXT_CAT;
                    else {
                        plate.moveTo(300, 150);
                        stage = Stage.MOVE_PLATE;
                    }
                }
                break;
            case SELECT_NEXT_CAT:
                // выбор следующего голодного кота
                catCurrent = getNextHungryCat();
                if (catCurrent == null || plate.isPlateEmpty()) {
                    // все коты накормлены, тарелку на базу. или тарелка пуста
                    plate.moveTo(300, 150);
                } else {
                    // к нему поехала тарелочка
                    plate.moveTo((int) catCurrent.getX() + 60, (int) catCurrent.getY() + 75);
                }
                stage = Stage.MOVE_PLATE;
                break;
            case MOVE_PLATE:
                if (!plate.isBusy()) {
                    if (plate.isPlateEmpty()) {
                        stage = Stage.PLATE_IS_EMPTY;
                    } else {
                        if (catCurrent == null) {
                            stage = Stage.PLATE_IS_EMPTY; // тарелка приехала на базу
                        } else {
                            catCurrent.eat(plate);
                            stage = Stage.CAT_EATING; // кот кушает
                        }
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

        // обновим состояние всех сущностей
        plate.update(timeDelay);
        for (Entity entity : cats)
            entity.update(timeDelay);
    }

    private Cat getNextHungryCat() {
        Cat cat = null;
        boolean allCatsNotHungry = true;
        for (int i = 0; i < cats.length; i++) {
            catNum++;
            if (catNum >= cats.length) catNum = 0;
            cat = cats[catNum];
            allCatsNotHungry = allCatsNotHungry && !cat.isHungry();
            if (!allCatsNotHungry) break; // найден голодный котик
        }
        return (allCatsNotHungry) ? null : cat;
    }

    GameField() {
        setBackground(Color.darkGray);
        setPreferredSize(new Dimension(W_WIDTH, W_HEIGHT));
        setDoubleBuffered(true);

        init();
        stage = Stage.IDLE;

        // реакция на клики мышью
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                // попали ли по тарелке?
                if (plate.contain(x, y)) {
                    // пополнить можно только пустую тарелку
                    if (plate.isPlateEmpty()) {
                        int rndCapacity = (int) (15 + Math.random() * 15);
                        plate.setCapacity(rndCapacity);
                    }
                }
            }
        });

        // пусть при наведении на тарелку рисуется рамка. прикольно же
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                plate.setShowBorder(plate.contain(x, y));
            }
        });
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
        plate.render(g);
        for (Entity entity : cats)
            entity.render(g);
    }

    // тело потока
    @Override
    public void run() {
        music();

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
    }

    // создаем объекты приложения
    private void init() {
        // создаем тарелку
        plate = new Plate();
        plate.setLocation(300, 150);

        // Создаем котов с разным аппетитом от 5 до 25
        for (int i = 0; i < CATS_COUNT; i++) {
            Cat cat = new Cat("Cat-" + i);
            cat.setLocation(10, (cat.getHeight() + 20) * i + 20);
            cats[i] = cat;
        }
    }

    // background music
    private void music() {
        String filename = "mario.wav";
        InputStream in;
        AudioInputStream audioInputStream = null;
        try {
            in = getClass().getClassLoader().getResourceAsStream(filename);
            audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(in));
        } catch (IllegalArgumentException e) {
            System.out.println("File not found");
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Clip clip;
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.setLoopPoints(0, -1);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
