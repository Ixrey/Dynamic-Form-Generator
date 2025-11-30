import javax.swing.SwingUtilities;

import controller.FormController;
import gui.MainWindow;

public class App {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            FormController controller = new FormController(window);
            window.setVisible(true);
        });
    }
}
