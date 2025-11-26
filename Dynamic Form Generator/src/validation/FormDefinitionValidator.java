package validation;

import java.util.ArrayList;
import java.util.List;

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

        for (int i = 0; i < fields.size(); i++) {
            JsonNode field = fields.get(i);

            if (!field.has("id") || !field.get("id").isTextual()) {
                errors.add("Feld " + i + ": id fehlt oder ist kein String");
            }

            if (!field.has("label") || !field.get("label").isTextual()) {
                errors.add("Feld " + i + ": label fehlt oder ist kein String");
            }

            if (!field.has("controlType") || !field.get("controlType").isTextual()) {
                errors.add("Feld " + i + ": controlType fehlt oder ist kein String");
            } else {
                String controlTypeText = field.get("controlType").asText();
                ControlType controlTypeEnum = null;

                try {
                    controlTypeEnum = ControlType.valueOf(controlTypeText.toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.add("Ungültiger controlType " + controlTypeText);
                    continue;
                }

                if (controlTypeEnum == ControlType.DROPDOWN) {
                    JsonNode optionsNode = fields.get("options");

                    if (optionsNode == null || !optionsNode.isArray() || optionsNode.size() == 0) {
                        errors.add(
                                "Feld " + i + ": Für controlType Dropdown muss options ein nicht-leeres Array sein.");
                    } else {
                        for (int j = 0; j < optionsNode.size(); j++) {
                            JsonNode option = optionsNode.get(j);
                            if (!option.isTextual()) {
                                errors.add("Feld " + i + ": Option " + j + " in options ist kein String");
                            }
                        }
                    }
                }
            }

            if (!field.has("dataType") || !field.get("dataType").isTextual()) {
                errors.add("Feld " + i + ": dataType fehlt oder ist kein String");
            } else {
                String dataType = field.get("dataType").asText();

                try {
                    DataType.valueOf(dataType.toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.add("Ungültiger dataType " + dataType);
                }
            }

            if (!field.has("required") || !field.get("required").isBoolean()) {
                errors.add("Feld " + i + ": required fehlt oder ist kein Boolean");
            }

        }

        return errors;
    }
}
