package gui;

import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.BorderLayout;

public class MainWindow extends JFrame {
    JPanel formContainer;
    JButton btnLoadForm;
    JButton btnLoadResult;
    JButton btnSaveResult;

    public MainWindow() {
        super("Dynamischer Formular Generator");

        initWindow();
        initLayout();
    }

    public void initWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    public void initLayout() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnLoadForm = new JButton("Formular Laden");
        btnLoadResult = new JButton("Ergebnis Speichern");
        btnSaveResult = new JButton("Ergebnis Laden");

        toolBar.add(btnLoadForm);
        toolBar.add(btnLoadResult);
        toolBar.add(btnSaveResult);

        formContainer = new JPanel(new BorderLayout());

        JLabel lblText = new JLabel("test");

        formContainer.add(lblText, BorderLayout.CENTER);
        contentPane.add(formContainer, BorderLayout.CENTER);
        contentPane.add(toolBar, BorderLayout.NORTH);
    }

    public void showFormPanel(JPanel panel) {
        formContainer.removeAll();
        formContainer.add(panel, BorderLayout.CENTER);
        formContainer.revalidate();
        formContainer.repaint();
    }
}
