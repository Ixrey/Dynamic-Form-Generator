package gui;

import model.FormDefinition;
import model.FormField;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FormGuiBuilder {
    private final Map<String, JComponent> componentsById = new LinkedHashMap<>();
    private FormDefinition currentFormDefinition;

    public JPanel buildFormPanel(FormDefinition formDefinition) {
        this.currentFormDefinition = formDefinition;
        componentsById.clear();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 50);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        List<FormField> fields = formDefinition.getFields();

        for (FormField field : fields) {
            boolean required = field.isRequired();
            String labelText = field.getLabel();
            JLabel lblField;
            JComponent controlType = createControlTypeForField(field);

            if (required) {
                lblField = new JLabel(labelText + " *");
            } else {
                lblField = new JLabel(labelText);
            }

            componentsById.put(field.getId(), controlType);

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0;
            panel.add(lblField, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            panel.add(controlType, gbc);

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
                return createDateSpinner();
            case DROPDOWN:
                return new JComboBox<>(field.getOptions().toArray(new String[field.getOptions().size()]));
            case SPINNER:
                return createNumberSpinner();
            default:
                throw new IllegalArgumentException("Nicht unterstützer controlType: " + field.getControlType());
        }
    }

    public JSpinner createDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd.MM.yyyy");
        spinner.setEditor(editor);
        return spinner;
    }

    public JSpinner createNumberSpinner() {
        SpinnerNumberModel model = new SpinnerNumberModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner);
        spinner.setEditor(editor);
        return spinner;
    }

    public Map<String, Object> getCurrentValues() {
        Map<String, Object> values = new LinkedHashMap<>();

        for (FormField field : currentFormDefinition.getFields()) {
            String id = field.getId();
            String label = field.getLabel();

            JComponent component = componentsById.get(id);
            if (component == null) {
                continue;
            }

            Object value = extractValue(field, component);
            values.put(label, value);
        }
        return values;
    }

    private Object extractValue(FormField field, JComponent component) {
        switch (field.getControlType()) {
            case TEXTFIELD:
            case TEXTAREA:
                return ((JTextComponent) component).getText();
            case CHECKBOX:
                return ((JCheckBox) component).isSelected();
            case DATE:
            case SPINNER:
                return ((JSpinner) component).getValue();
            case DROPDOWN:
                return ((JComboBox<?>) component).getSelectedItem();
            default:
                return null;
        }
    }

    public void applyValues(Map<String, Object> values) {
        if (currentFormDefinition == null) {
            return;
        }

        for (FormField field : currentFormDefinition.getFields()) {
            String label = field.getLabel();
            String id = field.getId();

            Object value = values.get(label);

            if (value == null) {
                continue;
            }

            JComponent component = componentsById.get(id);

            if (component == null) {
                continue;
            }

            applyValueToComponent(field, component, value);
        }
    }

    private void applyValueToComponent(FormField field, JComponent component, Object value) {
        switch (field.getControlType()) {
            case TEXTFIELD:
            case TEXTAREA:
                if (component instanceof JTextComponent textComp) {
                    textComp.setText(value != null ? value.toString() : "");
                }
                break;
            case CHECKBOX:
                if (component instanceof JCheckBox checkBox) {
                    if (value instanceof Boolean b) {
                        checkBox.setSelected(b);
                    } else {
                        checkBox.setSelected(Boolean.parseBoolean(value.toString()));
                    }
                }
                break;
            case DROPDOWN:
                if (component instanceof JComboBox<?> comboBox) {
                    comboBox.setSelectedItem(value);
                }
                break;
            case SPINNER:
                if (component instanceof JSpinner spinner) {
                    spinner.setValue(value);
                }
                break;
            case DATE:
                if (component instanceof JSpinner spinner) {
                    Object val = value;
                    if (val instanceof Long l) {
                        spinner.setValue(new Date(l));
                    } else if (val instanceof Date d) {
                        spinner.setValue(d);
                    } else if (val instanceof String s) {
                        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                        try {
                            spinner.setValue(df.parse(s));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            default:
        }

    }
}
