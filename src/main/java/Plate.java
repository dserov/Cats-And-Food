/**
 * Plate
 *
 * @author DSerov
 * @version dated 17 feb, 2018
 * @link https://github.com/dserov/CatsAndFood
 */

public class Plate {
    private int food; // объем тарелки. от 50 до 100

    Plate() {
        reinit();
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
    boolean isEmpty() {
        return food == 0;
    }
}
