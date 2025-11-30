package test;

import gui.FormGuiBuilder;
import gui.MainWindow;
import model.ControlType;
import model.DataType;
import model.FormDefinition;
import model.FormField;

import javax.swing.*;
import java.util.Arrays;

public class FormCreatorTest {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // 1. Main Window starten
            MainWindow window = new MainWindow();
            window.setVisible(true);

            // 2. Demo-FormDefinition erstellen
            FormField name = new FormField();
            name.setId("name");
            name.setLabel("Name");
            name.setControlType(ControlType.TEXTFIELD);
            name.setDataType(DataType.STRING);
            name.setRequired(true);

            // FormField age = new FormField();
            // age.setId("age");
            // age.setLabel("Alter");
            // age.setControlType(ControlType.SPINNER);
            // age.setDatatype(DataType.INT);
            // age.setRequired(false);

            FormField birthday = new FormField();
            birthday.setId("birthday");
            birthday.setLabel("Geburtsdatum");
            birthday.setControlType(ControlType.DATE);
            birthday.setDataType(DataType.STRING);
            birthday.setRequired(false);

            FormField newsletter = new FormField();
            newsletter.setId("newsletter");
            newsletter.setLabel("Newsletter");
            newsletter.setControlType(ControlType.CHECKBOX);
            newsletter.setDataType(DataType.BOOLEAN);
            newsletter.setRequired(false);

            FormField rating = new FormField();
            rating.setId("rating");
            rating.setLabel("Bewertung");
            rating.setControlType(ControlType.DROPDOWN);
            rating.setDataType(DataType.STRING);
            rating.setRequired(true);
            rating.setOptions(Arrays.asList("Sehr gut", "Gut", "Mittel", "Schlecht"));

            FormDefinition def = new FormDefinition();
            def.setFormTitle("Demo-Formular");
            def.setFields(Arrays.asList(name, birthday, newsletter, rating));

            // 3. Formular generieren
            FormGuiBuilder builder = new FormGuiBuilder();
            JPanel demoPanel = builder.buildFormPanel(def);

            // 4. Im Fenster anzeigen
            window.showFormPanel(demoPanel);
        });
    }
}
