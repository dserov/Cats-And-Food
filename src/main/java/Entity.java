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
    protected float dx; // by horizontal move
    protected float dy; // by vertical move
//    Image sprite;

    public Entity() {
        super();
    }

    abstract public void update(long timeDelay);
    abstract public void render(Graphics g);
}
