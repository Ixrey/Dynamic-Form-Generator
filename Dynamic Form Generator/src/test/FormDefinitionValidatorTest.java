package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import validation.FormDefinitionValidator;

public class FormDefinitionValidatorTest {

  private final ObjectMapper mapper = new ObjectMapper();
  private final FormDefinitionValidator validator = new FormDefinitionValidator();

  private JsonNode parse(String json) throws Exception {
    return mapper.readTree(json);
  }

  @Test
  void validFormDefinition_returnsNoErrors() throws Exception {
    String json = """
        {
          "formTitle": "Kunden-Feedback",
          "fields": [
            {
              "id": "name",
              "label": "Name",
              "controlType": "textfield",
              "dataType": "string",
              "required": true
            },
            {
              "id": "rating",
              "label": "Bewertung",
              "controlType": "dropdown",
              "dataType": "string",
              "required": true,
              "options": ["Sehr gut", "Gut"]
            }
          ]
        }
        """;

    JsonNode root = parse(json);
    List<String> errors = validator.validate(root);

    assertTrue(errors.isEmpty(), "Bei einer gültigen Formulardefinition dürfen keine Fehler auftreten");
  }

  @Test
  void missingFormTitle_addsError() throws Exception {
    String json = """
        {
          "fields": [
            {
              "id": "name",
              "label": "Name",
              "controlType": "textfield",
              "dataType": "string",
              "required": true
            },
            {
              "id": "rating",
              "label": "Bewertung",
              "controlType": "dropdown",
              "dataType": "string",
              "required": true,
              "options": ["Sehr gut", "Gut"]
            }
          ]
        }
        """;

    JsonNode root = parse(json);
    List<String> errors = validator.validate(root);

    assertFalse(errors.isEmpty(), "Es sollten Fehler auftreten wenn formTitle fehlt");

    boolean found = false;
    for (String message : errors) {
      if (message.equals("formTitle fehlt oder ist kein String")) {
        found = true;
        break;
      }
    }
    assertTrue(found, "Die Fehlermeldung wurde nicht gefudnen");
  }

  @Test
  void missingOptions_addsError() throws Exception {
    String json = """
        {
          "formTitle": "Kunden-Feedback",
          "fields": [
            {
              "id": "name",
              "label": "Name",
              "controlType": "textfield",
              "dataType": "string",
              "required": true
            },
            {
              "id": "rating",
              "label": "Bewertung",
              "controlType": "dropdown",
              "dataType": "string",
              "required": true
            }
          ]
        }
        """;
    JsonNode root = parse(json);
    List<String> errors = validator.validate(root);

    assertFalse(errors.isEmpty(), "Es sollten Fehler auftreten wenn Dropdown keine options hat");

    boolean found = false;
    for (String message : errors) {
      if (message.contains("Für controlType Dropdown muss options ein nicht-leeres Array sein.")) {
        found = true;
        break;
      }
    }

    assertTrue(found, "Die erwartete Fehlermeldung wurde nicht gefunden");
  }

  @Test
  void invalidDataType_addsError() throws Exception {
    String json = """
        {
          "formTitle": "Kunden-Feedback",
          "fields": [
            {
              "id": "name",
              "label": "Name",
              "controlType": "textfield",
              "dataType": "string",
              "required": true
            },
            {
              "id": "rating",
              "label": "Bewertung",
              "controlType": "dropdown",
              "dataType": "spinner",
              "required": true,
              "options": ["Sehr gut", "Gut"]
            }
          ]
        }
        """;
    JsonNode root = parse(json);
    List<String> errors = validator.validate(root);

    assertFalse(errors.isEmpty(), "Es sollten Fehler auftauchen, wenn der dataType nicht gültig ist.");

    boolean found = false;
    for (String message : errors) {
      if (message.contains("Ungültiger dataType: spinner")) {
        found = true;
        break;
      }
    }

    assertTrue(found, "Die erwartete Fehlermeldung wurde nicht gefunden.");
  }
}
