import java.awt.*;

/**
 * Графическая составляющая объектов
 *
 * @author DSerov
 * @version dated 17 feb, 2018
 * @link https://github.com/dserov/CatsAndFood
 */
public abstract class Entity {
    double dx; // by horizontal move
    double dy; // by vertical move
    double x, y, width, height;


    abstract public void update(long timeDelay);

    abstract public void render(Graphics g);

    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeight() {
        return height;
    }

    public double getCenterY() {
        return (y + height) / 2.0;
    }
}
