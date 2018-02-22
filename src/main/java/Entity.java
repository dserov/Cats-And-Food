import java.awt.*;
import java.awt.geom.Rectangle2D;


/**
 * Графическая составляющая объектов
 *
 * @author DSerov
 * @version dated 17 feb, 2018
 * @link https://github.com/dserov/CatsAndFood
 */
public abstract class Entity extends Rectangle2D.Double {
    double dx; // by horizontal move
    double dy; // by vertical move

    abstract public void update(long timeDelay);
    abstract public void render(Graphics g);

    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
