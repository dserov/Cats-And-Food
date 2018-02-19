import javax.swing.*;
import java.awt.*;

/**
 * Cats And Food
 *
 * @author DSerov
 * @version dated 17 feb, 2018
 * @link https://github.com/dserov/CatsAndFood
 */
public class CatsAndFood extends JFrame {
    public CatsAndFood() {
        // add game field
        add(new GameField());
        setResizable(false);
        pack();
        setTitle("Cats and Food");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new CatsAndFood();
            frame.setVisible(true);
        });
    }
}
