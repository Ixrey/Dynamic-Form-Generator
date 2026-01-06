package gui;

import model.FormDefinition;
import model.FormField;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.JTextComponent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FormGuiBuilder {
    private Map<String, JComponent> componentsById = new LinkedHashMap<>();
    private FormDefinition currentFormDefinition;

    public JPanel buildFormPanel(FormDefinition formDefinition) {
        this.currentFormDefinition = formDefinition;
        componentsById.clear();

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 15);
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
                lblField = new JLabel("<html>" + labelText + " <span style='color:red;'>*</span></html>");
            } else {
                lblField = new JLabel(labelText);
            }

            componentsById.put(field.getId(), controlType);

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0;
            fieldsPanel.add(lblField, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            fieldsPanel.add(controlType, gbc);

            row++;
        }

        JPanel panel = new JPanel(new BorderLayout(0, 10));

        JLabel titleLabel = new JLabel(formDefinition.getFormTitle());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        titleLabel.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(fieldsPanel, BorderLayout.CENTER);

        return panel;
    }

    public JComponent createControlTypeForField(FormField field) {
        switch (field.getControlType()) {
            case TEXTFIELD:
                JTextField textField = new JTextField();
                textField.setColumns(20);
                return textField;
            case TEXTAREA:
                JTextArea textArea = new JTextArea(3, 20);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                return textArea;
            case CHECKBOX:
                return new JCheckBox();
            case DATE:
                return createDateSpinner();
            case DROPDOWN:
                return createDropdown(field);
            case SPINNER:
                return createNumberSpinner();
            default:
                throw new IllegalArgumentException("Nicht unterstützer controlType: " + field.getControlType());
        }
    }

    private JComboBox<String> createDropdown(FormField field) {
        List<String> items = new ArrayList<>();
        items.add("-- Bitte wählen --");
        items.addAll(field.getOptions());

        JComboBox<String> combo = new JComboBox<>(items.toArray(new String[0]));
        combo.setSelectedIndex(0);
        return combo;
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

    public void markFieldInvalid(String fieldId, String errorMessage) {
        JComponent component = componentsById.get(fieldId);

        if (component != null) {
            component.setBorder(new LineBorder(Color.RED, 1));
            component.setToolTipText(errorMessage);
        }

    }

    public void clearValidationMarks() {
        for (JComponent comp : componentsById.values()) {
            comp.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
            comp.setToolTipText(null);
        }
    }
}
