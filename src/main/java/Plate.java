import sun.java2d.pipe.BufferedOpCodes;

import java.awt.*;

/**
 * Plate
 *
 * @author DSerov
 * @version dated 17 feb, 2018
 * @link https://github.com/dserov/CatsAndFood
 */

public class Plate extends Entity {
    private int food; // объем тарелки. от 50 до 100

    Plate() {
        super();
        reinit();
        // set size
        x = 0;
        y = 0;
        width = 70;
        height = 30;

        // скорость перемещения в пикселях в секунду
        dx = 50;
        dy = 50;
    }

    @Override
    public void update(long timeDelay) {
        x += (dx * timeDelay) / 1000;
        y += (dy * timeDelay) / 1000;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.blue);
        g.fillOval((int) x, (int) y, (int) width, (int) height);
    }

    public void reinit() {
        food = (int) (50 + Math.random() * 50);
    }

    /**
     * кот лизнул из миски. чтоб насытиться, надо лизнуть несколько раз
     *
     * @param n сколько нужно лизнуть
     * @return int сколько удалось лизнуть
     */
    int decreaseFood(int n) {
        if (isEmpty())
            return 0; // тарелка пуста. в бесконечном цикле рискуем пролизать дырку
        if (n > food) {
            n = food; // удалось лизнуть меньше необходимого
            food = 0; // Доел тарелку
        } else
            this.food -= n;
        return n;
    }

    public void info() {
        System.out.println(this.getClass().getName() + ": " + food);
    }

    /**
     * наполнить тарелку
     *
     * @param food колво еды
     */
    public void setFood(int food) {
        this.food = food;
    }

    /**
     * пустая ли тарелка ?
     *
     * @return true пустая
     */
    boolean isPlateEmpty() {
        return food == 0;
    }
}
