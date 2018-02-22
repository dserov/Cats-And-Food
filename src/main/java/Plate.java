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

    // тарелка должна знать, куда она едет
    private double xDest, yDest;

    // если тарелка куда-то движется - она занята, как приедет - свободна для общения
    private boolean busy = false;

    // установка цели для тарелки
    public void moveTo(int xDest, int yDest) {
        this.xDest = xDest;
        this.yDest = yDest;
        busy = true;
    }

    public boolean isBusy() {
        return busy;
    }

    Plate() {
        super();
        reinit();

        // скорость перемещения в пикселях в секунду
//        dx = 50;
//        dy = 50;
    }

    @Override
    public void update(long timeDelay) {
        if (!busy) {
            // тарелка никуда не едет
            return;
        }

        // проверка, что не у цели еще
        double x1 = x - xDest;
        double y1 = y - yDest;
        int distance = (int) Math.sqrt(x1 * x1 + y1 * y1);
        if (distance == 0) {
            // с прибытием
            busy = false;
            dx = 0;
            dy = 0;
            x = xDest;
            y = yDest;
            return;
        }

        if (dx == 0 && dy == 0) {
            // расчет направления движения и скорости
            dx = xDest - x;
            dy = yDest - y;
        }

        x += (dx * timeDelay) / 1000;
        y += (dy * timeDelay) / 1000;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.cyan);
        g.fillOval((int) x, (int) y, (int) width, (int) height);
    }

    public void reinit() {
        food = (int) (50 + Math.random() * 50);

        width = 70;
        height = 30;
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

    @Override
    public String toString() {
        return getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height +
                ",dx=" + dx + ",dy=" + dy + ",xDest=" + xDest + ",yDest=" + yDest + "]";

    }
}
