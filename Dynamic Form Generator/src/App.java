import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import controller.FormController;
import gui.MainWindow;

public class App {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
            }
            MainWindow window = new MainWindow();
            FormController controller = new FormController(window);
            window.setVisible(true);
        });
    }
}
