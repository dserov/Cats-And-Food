/**
 * Cat
 *
 * @author DSerov
 * @version dated 17 feb, 2018
 * @link https://github.com/dserov/CatsAndFood
 */

public class Cat {
    private String name; // котоимя
    private int appetite; // сколько кот ест за раз. (2-5)
    private int hungry; // уровень голода кота. как только обнулится - кот не голоден (5-25)

    Cat(String name) {
        this.name = name;
        reinit();
    }

    public void eat(Plate plate) {
        int kolvo = plate.decreaseFood(appetite); // удалось лизнуть столько-то еды
        hungry -= kolvo;
        if (hungry < 0) hungry = 0;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " { name=" + name + ", appetite=" + appetite + ", isHungry= " + isHangry() + " }";
    }

    /**
     * реинициализация объекта начальными значениями аппетита(сколько съест за раз) и уровнем голода
     */
    public void reinit() {
        appetite = (int) (2 + Math.random() * 3);
        hungry = (int) (5 + Math.random() * 20);
    }

    /**
     * кот голоден?
     * @return true голоден
     */
    boolean isHangry() {
        return hungry > 0;
    }
}
