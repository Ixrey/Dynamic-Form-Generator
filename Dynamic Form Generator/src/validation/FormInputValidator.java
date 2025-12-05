package validation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.ControlType;
import model.DataType;
import model.FormDefinition;
import model.FormField;

public class FormInputValidator {

    public Map<String, String> validate(FormDefinition definition, Map<String, Object> values) {
        Map<String, String> errors = new HashMap<>();

        if (definition == null) {
            errors.put("GLOBAL", "Keine Formulardefinition vorhanden.");
            return errors;
        }

        if (values == null) {
            errors.put("GLOBAL", "Es wurden keine Eingabewerte übergeben.");
            return errors;
        }

        for (FormField field : definition.getFields()) {
            String label = field.getLabel();
            Object value = values.get(label);

            boolean required = field.isRequired();
            boolean isEmpty = value == null || (value instanceof String s && s.isBlank());

            // --- Pflichtfeldprüfung ---
            if (isEmpty) {
                if (required) {
                    errors.put(field.getId(),
                            "Pflichtfeld \"" + label + "\" wurde nicht ausgefüllt.");
                }
                continue; // keine weiteren Prüfungen nötig
            }

            // --- Datentypprüfung ---
            validateDataType(field, value, errors);

            // --- Dropdownprüfung ---
            if (field.getControlType() == ControlType.DROPDOWN) {
                validateDropdownValue(field, value, errors);
            }
        }

        return errors;
    }

    private void validateDataType(FormField field, Object value, Map<String, String> errors) {
        String label = field.getLabel();
        DataType dataType = field.getDataType();

        switch (dataType) {
            case STRING:
                break;

            case INT:
                if (value instanceof Number)
                    break;
                if (value instanceof String s) {
                    try {
                        Integer.parseInt(s.trim());
                    } catch (NumberFormatException e) {
                        errors.put(field.getId(),
                                "Feld \"" + label + "\" erwartet eine ganze Zahl.");
                    }
                    break;
                }
                errors.put(field.getId(),
                        "Feld \"" + label + "\" erwartet eine ganze Zahl.");
                break;

            case FLOAT:
                if (value instanceof Number)
                    break;
                if (value instanceof String s2) {
                    try {
                        Double.parseDouble(s2.trim().replace(',', '.'));
                    } catch (NumberFormatException e) {
                        errors.put(field.getId(),
                                "Feld \"" + label + "\" erwartet eine Kommazahl.");
                    }
                    break;
                }
                errors.put(field.getId(),
                        "Feld \"" + label + "\" erwartet eine Kommazahl.");
                break;

            case BOOLEAN:
                if (value instanceof Boolean)
                    break;
                if (value instanceof String s3) {
                    String lower = s3.trim().toLowerCase();
                    if (!lower.equals("true") && !lower.equals("false")) {
                        errors.put(field.getId(),
                                "Feld \"" + label + "\" erwartet true oder false.");
                    }
                    break;
                }
                errors.put(field.getId(),
                        "Feld \"" + label + "\" erwartet true oder false.");
                break;

            case DATE:
                if (value instanceof Date || value instanceof Long || value instanceof String) {
                    break;
                }
                errors.put(field.getId(),
                        "Feld \"" + label + "\" erwartet ein Datum.");
                break;
        }
    }

    private void validateDropdownValue(FormField field, Object value, Map<String, String> errors) {
        if (value == null)
            return;
        if (field.getOptions() == null || field.getOptions().isEmpty())
            return;

        String valueText = value.toString();
        if (!field.getOptions().contains(valueText)) {
            errors.put(field.getId(),
                    "Wert \"" + valueText + "\" ist keine gültige Option für \"" + field.getLabel() + "\".");
        }
    }
}
