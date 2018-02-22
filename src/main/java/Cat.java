import java.awt.*;

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
    private int offsetFill; // смещение заполнения от начала прямоугольника
    private int heightFill; // высота заполнения


    Cat(String name) {
        this.name = name;
        width = 70;
        height = 70;
        reinit();
    }

    public void eat(Plate plate) {
        int kolvo = plate.decreaseFood(appetite); // удалось лизнуть столько-то еды
        hungry -= kolvo;
        if (hungry < 0) hungry = 0;
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
    }

    /**
     * кот голоден?
     * @return true голоден
     */
    boolean isHungry() {
        return hungry > 0;
    }

    @Override
    public void update(long timeDelay) {
        // расчет высоты заполнения и смещения от начала (верхнего левого угла)
        double coeff = getHeight() / initialHungry;
        heightFill = (int) ((initialHungry - hungry) * coeff);
        offsetFill = (int) (hungry * coeff);
    }

    @Override
    public void render(Graphics g) {
        // нарисуем состояние заполненности кота едой
        // смещение - hungry, вычислим высоту
        g.setColor(Color.blue);
        g.fillRect((int) getX(), (int) getY() + offsetFill, (int) getWidth(), heightFill);
        g.setColor(Color.white);
        g.drawRect((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
    }

    public boolean isBusy() {
        return busy;
    }
}
