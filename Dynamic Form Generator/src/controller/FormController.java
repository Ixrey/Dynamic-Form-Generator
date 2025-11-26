package controller;

import java.io.File;
import gui.FormGuiBuilder;
import io.JsonReader;
import validation.FormDefinitionValidator;

public class FormController {
    private final JsonReader jsonReader;
    private final FormDefinitionValidator validator;
    private FormGuiBuilder guiBuilder;
    private File file = new File("forms/Kunden-Feedback.json");

    public FormController(JsonReader jsonReader, FormDefinitionValidator validator, FormGuiBuilder guiBuilder) {
        this.jsonReader = jsonReader;
        this.validator = validator;
        this.guiBuilder = guiBuilder;
    }
}
