package gui;

import model.FormDefinition;
import model.FormField;

import java.util.List;

import javax.swing.*;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class FormGuiBuilder {

    public JPanel buildFormPanel(FormDefinition formDefinition) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        List<FormField> fields = formDefinition.getFields();

        for (FormField field : fields) {
            boolean required = field.isRequired();
            String labelText = field.getLabel();
            JLabel lblField;

            if (required) {
                lblField = new JLabel(labelText + " *");
            } else {
                lblField = new JLabel(labelText);
            }
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0;
            panel.add(lblField, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            panel.add(createControlTypeForField(field), gbc);

            row++;
        }
        return panel;
    }

    public JComponent createControlTypeForField(FormField field) {
        switch (field.getControlType()) {
            case TEXTFIELD:
                return new JTextField();
            case TEXTAREA:
                return new JTextArea();
            case CHECKBOX:
                return new JCheckBox();
            case DATE:
                SpinnerDateModel model = new SpinnerDateModel();
                JSpinner spinner = new JSpinner(model);
                JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd.mm.yyyy");
                spinner.setEditor(editor);
                return spinner;
            case DROPDOWN:
                return new JComboBox<>(field.getOptions().toArray(new String[field.getOptions().size()]));
        }
        throw new IllegalArgumentException();
    }
}
