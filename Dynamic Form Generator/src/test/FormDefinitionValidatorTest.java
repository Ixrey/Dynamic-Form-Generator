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

  private boolean containsError(List<String> errors, String expectedPart) {
    for (String message : errors) {
      if (message.contains(expectedPart)) {
        return true;
      }
    }
    return false;
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

    assertTrue(errors.isEmpty(),
        "Bei einer gültigen Formulardefinition dürfen keine Fehler auftreten");
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
            }
          ]
        }
        """;

    JsonNode root = parse(json);
    List<String> errors = validator.validate(root);

    assertFalse(errors.isEmpty(), "Es sollten Fehler auftreten, wenn formTitle fehlt");
    assertTrue(containsError(errors, "formTitle"),
        "Die Fehlermeldung zu fehlendem formTitle wurde nicht gefunden");
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

    assertFalse(errors.isEmpty(),
        "Es sollten Fehler auftreten, wenn Dropdown keine options hat");
    assertTrue(containsError(errors, "options"),
        "Fehlermeldung zu fehlenden options wurde nicht gefunden");
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

    assertFalse(errors.isEmpty(),
        "Es sollten Fehler auftreten, wenn der dataType ungültig ist");
    assertTrue(containsError(errors, "dataType"),
        "Die Fehlermeldung zu ungültigem dataType wurde nicht gefunden");
  }

  @Test
  void missingId_addsError() throws Exception {
    String json = """
        {
          "formTitle": "Kunden-Feedback",
          "fields": [
            {
              "label": "Name",
              "controlType": "textfield",
              "dataType": "string",
              "required": true
            }
          ]
        }
        """;

    JsonNode root = parse(json);
    List<String> errors = validator.validate(root);

    assertFalse(errors.isEmpty(),
        "Es sollten Fehler auftreten, wenn id fehlt");
    assertTrue(containsError(errors, "id"),
        "Fehlermeldung zu fehlender id wurde nicht gefunden");
  }

  @Test
  void invalidControlType_addsError() throws Exception {
    String json = """
        {
          "formTitle": "Kunden-Feedback",
          "fields": [
            {
              "id": "name",
              "label": "Name",
              "controlType": "supertextfield",
              "dataType": "string",
              "required": true
            }
          ]
        }
        """;

    JsonNode root = parse(json);
    List<String> errors = validator.validate(root);

    assertFalse(errors.isEmpty(),
        "Es sollten Fehler bei ungültigem controlType auftreten");
    assertTrue(containsError(errors, "controlType"),
        "Fehlermeldung zu ungültigem controlType wurde nicht gefunden");
  }

  @Test
  void duplicateIds_addError() throws Exception {
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
              "id": "name",
              "label": "Name wiederholt",
              "controlType": "textfield",
              "dataType": "string",
              "required": false
            }
          ]
        }
        """;

    JsonNode root = parse(json);
    List<String> errors = validator.validate(root);

    assertFalse(errors.isEmpty(),
        "Es sollten Fehler auftreten, wenn die id doppelt vergeben wurde");
    assertTrue(containsError(errors, "doppelt"),
        "Fehlermeldung zu doppelter id wurde nicht gefunden");
  }

  @Test
  void missingRequired_behavesAsSpecified() throws Exception {
    String json = """
        {
          "formTitle": "Kunden-Feedback",
          "fields": [
            {
              "id": "name",
              "label": "Name",
              "controlType": "textfield",
              "dataType": "string"
            }
          ]
        }
        """;

    JsonNode root = parse(json);
    List<String> errors = validator.validate(root);

    assertTrue(containsError(errors, "required"),
        "Fehlermeldung zu fehlendem required wurde nicht gefunden");
  }
}
