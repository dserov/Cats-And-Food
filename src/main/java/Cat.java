import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Cat
 *
 * @author DSerov
 * @version dated 17 feb, 2018
 * @link https://github.com/dserov/CatsAndFood
 */

public class Cat extends Entity {
    private String name; // котоимя
    private int appetite; // сколько кот ест за раз. (2-5)
    private int hungry; // уровень голода кота. как только обнулится - кот не голоден (5-25)
    private boolean busy; // кот занят обычно анимацией
    private int initialHungry; // начальная голодность кота. нужно для вычисления заполненности кота едой
    private double coeff; // коэфициент для расчета прогрессбара

    private final static String SPRITEFILENAME = "images/cat%d.png";
    private static ArrayList<Image> sprites = new ArrayList<>();
    private int frameCurrent = -1; // текущий фрейм
    private long totalFrameTime; // время с последней смены кадра
    private long interFrametime = 100; // время между кадрами ms
    private int animationDuration = 18; // аниамция идет с 9-го до 9-го кадра

    Cat(String name) {
        this.name = name;

        loadImages();
        if (sprites.size() > 0) {
            frameCurrent = 7;
            width = sprites.get(0).getWidth(null);
            height = sprites.get(0).getHeight(null);
        } else {
            width = 70;
            height = 70;
        }

        reinit();
    }

    public void eat(Plate plate) {
        int kolvo = plate.decreaseFood(appetite); // удалось лизнуть столько-то еды
        hungry -= kolvo;
        if (hungry < 0) hungry = 0;

        //
        busy = true;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " { name=" + name + ", appetite=" + appetite + ", isHungry= " + isHungry() + " }";
    }

    /**
     * реинициализация объекта начальными значениями аппетита(сколько съест за раз) и уровнем голода
     */
    public void reinit() {
        appetite = (int) (2 + Math.random() * 3);
        initialHungry = hungry = (int) (5 + Math.random() * 20);
        coeff = height / initialHungry;
    }

    /**
     * кот голоден?
     *
     * @return true голоден
     */
    boolean isHungry() {
        return hungry > 0;
    }

    @Override
    public void update(long timeDelay) {
        // анимация
        if (busy) {
            totalFrameTime += timeDelay;
            if (totalFrameTime >= interFrametime) {
                totalFrameTime = 0;
                if (frameCurrent >= 0) {
                    frameCurrent++;
                    if (frameCurrent >= sprites.size())
                        frameCurrent = 0;
                    // длительность анимации (кол-во кадров)
                    animationDuration--;
                    if (animationDuration <= 0 && frameCurrent == 7) {
                        animationDuration = 18;
                        busy = false;
                    }
                }
            }
        }

    }

    @Override
    public void render(Graphics g) {
        if (frameCurrent >= 0 && frameCurrent < sprites.size())
            g.drawImage(sprites.get(frameCurrent), (int) x + 15, (int) y, null);
        else
            System.out.println(this.name + ", frameCurrent=" + frameCurrent);

        // нарисуем состояние заполненности кота едой
        // расчет высоты заполнения и смещения от начала (верхнего левого угла)
        int heightFill = (int) ((initialHungry - hungry) * coeff);
        int offsetFill = (int) (hungry * coeff);
        g.setColor(Color.blue);
        g.fillRect((int) x, (int) y + offsetFill, 10, heightFill);
        g.setColor(Color.white);
        g.drawRect((int) x, (int) y, 10, (int) height);

        // cat's name
        g.drawString(name, (int) x + 30, (int) y + 10);
    }

    public boolean isBusy() {
        return busy;
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
    }
}
