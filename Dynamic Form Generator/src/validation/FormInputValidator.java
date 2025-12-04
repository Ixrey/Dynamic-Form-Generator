package validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import model.ControlType;
import model.DataType;
import model.FormDefinition;
import model.FormField;

public class FormInputValidator {
    public List<String> validate(FormDefinition definition, Map<String, Object> values) {
        List<String> errors = new ArrayList<>();

        if (definition == null) {
            errors.add("Keine Formulardefinition vorhanden.");
            return errors;
        }

        if (values == null) {
            errors.add("Es wurden keine Eingabewerte übergeben.");
            return errors;
        }

        for (FormField field : definition.getFields()) {
            String label = field.getLabel();
            boolean required = field.isRequired();
            DataType dataType = field.getDataType();
            ControlType controlType = field.getControlType();

            Object value = values.get(label);

            boolean isEmpty = value == null ||
                    (value instanceof String s && s.isBlank());

            if (isEmpty) {
                if (required) {
                    errors.add("Pflichtfeld \"" + label + "\" wurde nicht ausgefüllt.");
                }
                continue;
            }

            validateDataType(label, dataType, value, errors);

            if (controlType == ControlType.DROPDOWN) {
                validateDropdownValue(field, value, errors);
            }
        }
        return errors;
    }

    private void validateDataType(String label, DataType dataType, Object value, List<String> errors) {
        switch (dataType) {
            case STRING:
                break;
            case INT:
                if (value instanceof Number) {
                    break;
                }
                if (value instanceof String s) {
                    try {
                        Integer.parseInt(s.trim());
                    } catch (NumberFormatException e) {
                        errors.add("Feld \"" + label + "\" erwartet eine ganze Zahl.");
                    }
                    break;
                }
                errors.add("Feld \"" + label + "\" erwartet eine ganze Zahl.");
                break;
            case FLOAT:
                if (value instanceof Number) {
                    break;
                }
                if (value instanceof String s) {
                    String normalized = s.trim().replace(',', '.');
                    try {
                        Double.parseDouble(normalized);
                    } catch (NumberFormatException e) {
                        errors.add("Feld \"" + label + "\" erwartet eine Kommazahl.");
                    }
                    break;
                }
                errors.add("Feld \"" + label + "\" erwartet eine Kommazahl.");
                break;

            case BOOLEAN:
                if (value instanceof Boolean) {
                    break;
                }
                if (value instanceof String s) {
                    String lower = s.trim().toLowerCase();
                    if (!lower.equals("true") && !lower.equals("false")) {
                        errors.add("Feld \"" + label + "\" erwartet einen Wahrheitswert (true/false).");
                    }
                    break;
                }
                errors.add("Feld \"" + label + "\" erwartet einen Wahrheitswert (true/false).");
                break;

            case DATE:

                if (value instanceof Date) {
                    break;
                }
                if (value instanceof Long) {
                    break;
                }
                if (value instanceof String) {
                    break;
                }
                errors.add("Feld \"" + label + "\" erwartet ein Datum.");
                break;

            default:
                break;
        }
    }

    private void validateDropdownValue(FormField field, Object value, List<String> errors) {
        if (value == null) {
            return;
        }

        if (field.getOptions() == null || field.getOptions().isEmpty()) {
            return;
        }

        String valueText = value.toString();
        if (!field.getOptions().contains(valueText)) {
            errors.add("Wert \"" + valueText + "\" ist keine gültige Option für Feld \"" + field.getLabel() + "\".");
        }
    }
}
