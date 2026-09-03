package detective;

import detective.logic.Game;
import detective.ui.GameWindow;

import java.awt.EventQueue;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Game game = new Game();
            GameWindow window = new GameWindow(game);
            window.setVisible(true);
        });
    }
}
