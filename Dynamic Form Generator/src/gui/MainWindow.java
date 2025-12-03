package gui;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;

public class MainWindow extends JFrame {
    JPanel formContainer;
    JButton btnLoadForm;
    JButton btnLoadResult;
    JButton btnSaveResult;

    public MainWindow() {
        super("Dynamischer Formular Generator");

        initializeWindow();
        initializeLayout();
    }

    public void initializeWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    public void initializeLayout() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnLoadForm = new JButton("Formular Laden");
        btnLoadResult = new JButton("Ergebnis Laden");
        btnSaveResult = new JButton("Ergebnis Speichern");

        toolBar.add(btnLoadForm);
        toolBar.add(btnSaveResult);
        toolBar.add(btnLoadResult);

        formContainer = new JPanel(new BorderLayout());

        JLabel lblText = new JLabel("test");

        formContainer.add(lblText, BorderLayout.CENTER);
        contentPane.add(formContainer, BorderLayout.CENTER);
        contentPane.add(toolBar, BorderLayout.SOUTH);
    }

    public void showFormPanel(JPanel panel) {
        formContainer.removeAll();
        formContainer.add(panel, BorderLayout.CENTER);
        formContainer.revalidate();
        formContainer.repaint();
    }

    public void addLoadFormListener(ActionListener listener) {
        btnLoadForm.addActionListener(listener);
    }

    public void addLoadResultListener(ActionListener listener) {
        btnLoadResult.addActionListener(listener);
    }

    public void addSaveResultListener(ActionListener listener) {
        btnSaveResult.addActionListener(listener);
    }

    public void showValidationErrors(List<String> errors) {
        String message = String.join("\n", errors);
        JOptionPane.showMessageDialog(this, message, "Fehler beim Laden", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    public File chooseFormToOpen() {
        JFileChooser chooser = new JFileChooser(new File("forms"));
        chooser.setDialogTitle("Formulardatei auswählen");

        chooser.setFileFilter(new FileNameExtensionFilter("JSON-Dateien", "json"));

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }
}
