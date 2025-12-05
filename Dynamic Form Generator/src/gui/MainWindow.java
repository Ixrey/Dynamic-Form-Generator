package gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
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
        setSize(700, 500);
        setLocationRelativeTo(null);
    }

    public void initializeLayout() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        JLabel hint = new JLabel("* Pflichtfeld");
        hint.setBorder(new EmptyBorder(0, 10, 5, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLoadForm = new JButton("Formular Laden");
        btnLoadResult = new JButton("Ergebnis Laden");
        btnSaveResult = new JButton("Ergebnis Speichern");

        buttonPanel.add(btnLoadForm);
        buttonPanel.add(btnSaveResult);
        buttonPanel.add(btnLoadResult);

        Dimension btnSize = new Dimension(150, 30);
        btnLoadForm.setPreferredSize(btnSize);
        btnSaveResult.setPreferredSize(btnSize);
        btnLoadResult.setPreferredSize(btnSize);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(hint, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        formContainer = new JPanel(new BorderLayout());
        formContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        formContainer.add(createWelcomePanel(), BorderLayout.CENTER);
        contentPane.add(formContainer, BorderLayout.CENTER);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Dynamischer Formular-Generator");
        title.setFont(title.getFont().deriveFont(20f));

        JLabel subtitle = new JLabel(
                "<html>" +
                        "Wählen Sie über <b>\"Formular laden\"</b> eine Formulardefinition (JSON) aus.<br>" +
                        "Über <b>\"Ergebnis laden\"</b> können Sie gespeicherte Eingaben wieder einlesen." +
                        "</html>");

        title.setAlignmentX(LEFT_ALIGNMENT);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        panel.add(subtitle);

        return panel;
    }

    public void showFormPanel(JPanel panel) {
        formContainer.removeAll();

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);

        formContainer.add(scrollPane, BorderLayout.CENTER);
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

    public File chooseResultFileToSave() {
        JFileChooser chooser = new JFileChooser(new File("results"));
        chooser.setDialogTitle("Ergebnisdatei Speichern");

        chooser.setFileFilter(new FileNameExtensionFilter("JSON-Dateien", "json"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            if (!file.getName().toLowerCase().endsWith(".json")) {
                file = new File(file.getParentFile(), file.getName() + ".json");
            }
            return file;
        }
        return null;
    }

    public File chooseResultFileToOpen() {
        JFileChooser chooser = new JFileChooser(new File("results"));
        chooser.setDialogTitle("Ergebnisdatei auswählen");

        chooser.setFileFilter(new FileNameExtensionFilter("JSON-Dateien", "json"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }
}
