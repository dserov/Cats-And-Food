import java.util.Random;

/**
 * Cats And Food
 *
 *
 * @author DSerov
 * @version dated 17 feb, 2018
 * @link https://github.com/dserov/CatsAndFood
 */
public class CatsAndFood {
    final private int CATS_COUNT = 5;
    private Cat[] cats = new Cat[CATS_COUNT];
    private Plate plate;
    private Random random = new Random();

    public CatsAndFood() {
        // создаем котов и тарелку
        init();

        // инфа о тарелке
        plate.info();
        // кормим котов, пока тарелка не опустеет или все не накормятся
        boolean allCatsNotHungry; // признак, что все коты накормлены
        while(true) {
            allCatsNotHungry = true;
            for (Cat cat: cats ) {
                cat.eat(plate); // кот лизнул из тарелки
                allCatsNotHungry &= !cat.isHangry(); // если хоть один кот голоден, скинется в false
            }
            if (allCatsNotHungry || plate.isEmpty())
                break; // накормлены все, либо тарелка пуста
        }
        // вывод инфы
        for (Cat cat: cats )
            System.out.println(cat); // статус кота
        plate.info(); // статус тарелки
    }

    public static void main(String[] args) {
        new CatsAndFood();
    }

    private void init() {
        // Создаем котов с разным аппетитом от 5 до 25
        for (int i = 0; i < CATS_COUNT; i++)
            cats[i] = new Cat("Kot-" + i);
        plate = new Plate();
    }
}
