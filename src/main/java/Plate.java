import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Plate
 *
 * @author DSerov
 * @version dated 17 feb, 2018
 * @link https://github.com/dserov/CatsAndFood
 */

public class Plate extends Entity {
    private int capacity; // объем тарелки. от 50 до 100

    private int initialCapacity; // начальный объем
    private double coeff; // коэфициент для расчета прогрессбара

    // тарелка должна знать, куда она едет
    private double xDest, yDest;

    // если тарелка куда-то движется - она занята, как приедет - свободна для общения
    private boolean busy = false;

    private final static String SPRITEFILENAME = "images/tarelka%d.png";
    private static ArrayList<Image> sprites = new ArrayList<>();
    private int frameCurrent = -1; // текущий фрейм
    private long lastFrameTime; // время последней смены кадра
    private long interFrametime = 100; // время между кадрами ms

    private boolean showBorder = false;

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
        loadImages();
        if (sprites.size() > 0) {
            frameCurrent = 0;
            width = sprites.get(0).getWidth(null);
            height = sprites.get(0).getHeight(null);
        } else {
            width = 70;
            height = 30;
        }

        setCapacity(0);
    }

    @Override
    public void update(long timeDelay) {
        // анимация
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastFrameTime > interFrametime) {
            if (frameCurrent >= 0) {
                frameCurrent++;
                if (frameCurrent >= sprites.size())
                    frameCurrent = 0;
            }
            lastFrameTime = currentTimeMillis;
        }

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

    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
    }

    @Override
    public void render(Graphics g) {
        if (frameCurrent >= 0 && frameCurrent < sprites.size())
            g.drawImage(sprites.get(frameCurrent), (int) x, (int) y, null);

        // прогрессбар
        g.setColor(Color.blue);
        g.fillRect((int) x, (int) (y + height) + 2, (int) (capacity * coeff), 5);
        g.setColor(Color.white);
        g.drawRect((int) x , (int) (y + height) + 2, (int) (width), 5);

        if (showBorder) {
            g.setColor(Color.white);
            g.drawRoundRect((int) x - 5, (int) y - 5, (int) (width + 10), (int) (height + 15), 5, 5);
        }

        // пустая тарелка. покажем сообщение
        if (capacity == 0) {
            // пусть плавает, привлекает внимание
            g.drawString("Click me ->", (int) x + 50 + frameCurrent, (int) y + 15);
        }
    }

    /**
     * кот лизнул из миски. чтоб насытиться, надо лизнуть несколько раз
     *
     * @param n сколько нужно лизнуть
     * @return int сколько удалось лизнуть
     */
    int decreaseFood(int n) {
        if (isPlateEmpty())
            return 0; // тарелка пуста. в бесконечном цикле рискуем пролизать дырку
        if (n > capacity) {
            n = capacity; // удалось лизнуть меньше необходимого
            capacity = 0; // Доел тарелку
        } else
            this.capacity -= n;
        return n;
    }

    /**
     * наполнить тарелку
     *
     * @param capacity колво еды
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
        initialCapacity = capacity;
        if (initialCapacity == 0)
            coeff = width;
        else
            coeff = width / initialCapacity;
    }

    /**
     * пустая ли тарелка ?
     *
     * @return true пустая
     */
    boolean isPlateEmpty() {
        return capacity == 0;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height +
                ",dx=" + dx + ",dy=" + dy + ",xDest=" + xDest + ",yDest=" + yDest + "]";

    }

    private void loadImages() {
        if (SPRITEFILENAME.length() == 0)
            return;

        if (sprites.size() > 0)
            return;
        // картинки грузим
        try {
            Image img;
            int i = 1;
            while (i >= 0) {
                String fname = String.format(SPRITEFILENAME, i);
                img = ImageIO.read(getClass().getClassLoader().getResourceAsStream(fname));
                if (img != null)
                    sprites.add(img);
                i++;
            }
        } catch (IllegalArgumentException | IOException e) {
        }
        if (sprites.size() > 0)
            frameCurrent = 0;
    }


}
