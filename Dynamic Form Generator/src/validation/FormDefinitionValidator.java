package validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import model.ControlType;
import model.DataType;

public class FormDefinitionValidator {

    public List<String> validate(JsonNode root) {
        List<String> errors = new ArrayList<>();

        if (!root.has("formTitle") || !root.get("formTitle").isTextual()) {
            errors.add("formTitle fehlt oder ist kein String");
        }

        if (!root.has("fields") || !root.get("fields").isArray()) {
            errors.add("fields fehlt oder ist kein Array");
            return errors;
        }

        JsonNode fields = root.get("fields");

        Set<String> ids = new HashSet<>();

        for (int i = 0; i < fields.size(); i++) {
            JsonNode field = fields.get(i);

            if (field.has("id") && field.get("id").isTextual()) {
                String idValue = field.get("id").asText();

                if (!ids.add(idValue)) {
                    errors.add("Feld " + i + ": id '" + idValue + "' ist doppelt vergeben");
                }
            }
            validateField(fields.get(i), i, errors);
        }

        return errors;
    }

    private void validateField(JsonNode field, int index, List<String> errors) {
        requireText(field, "id", "Feld " + index + ": id fehlt oder ist kein String", errors);
        requireText(field, "label", "Feld " + index + ": label fehlt oder ist kein String", errors);
        validateControlType(field, index, errors);
        validateDataType(field, index, errors);
        requireBoolean(field, "required", "Feld " + index + ": required fehlt oder ist kein Boolean", errors);
    }

    private void validateControlType(JsonNode field, int index, List<String> errors) {
        if (!requireText(field, "controlType", "Feld " + index + ": controlType fehlt oder ist kein String", errors)) {
            return;
        }

        String controlTypeText = field.get("controlType").asText();
        ControlType controlTypeEnum = null;

        try {
            controlTypeEnum = ControlType.valueOf(controlTypeText.toUpperCase());
        } catch (IllegalArgumentException e) {
            errors.add("Ungültiger controlType " + controlTypeText);
            return;
        }

        if (controlTypeEnum == ControlType.DROPDOWN) {
            JsonNode optionsNode = field.get("options");

            if (optionsNode == null || !optionsNode.isArray() || optionsNode.size() == 0) {
                errors.add("Feld " + index + ": Für controlType Dropdown muss options ein nicht-leeres Array sein.");
            } else {
                for (int j = 0; j < optionsNode.size(); j++) {
                    JsonNode option = optionsNode.get(j);
                    if (!option.isTextual()) {
                        errors.add("Feld " + index + ": Option " + j + " in options ist kein String");
                    }
                }
            }
        }
    }

    private void validateDataType(JsonNode field, int index, List<String> errors) {
        if (!requireText(field, "dataType", "Feld " + index + ": dataType fehlt oder ist kein String", errors)) {
            return;
        }

        String dataType = field.get("dataType").asText();

        try {
            DataType.valueOf(dataType.toUpperCase());
        } catch (IllegalArgumentException e) {
            errors.add("Ungültiger dataType: " + dataType);
        }
    }

    private boolean requireText(JsonNode node, String fieldName, String message, List<String> errors) {
        if (!node.has(fieldName) || !node.get(fieldName).isTextual()) {
            errors.add(message);
            return false;
        }
        return true;
    }

    private boolean requireBoolean(JsonNode node, String fieldName, String message, List<String> errors) {
        if (!node.has(fieldName) || !node.get(fieldName).isBoolean()) {
            errors.add(message);
            return false;
        }
        return true;
    }
}
