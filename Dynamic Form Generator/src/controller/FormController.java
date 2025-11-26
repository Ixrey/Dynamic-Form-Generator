package controller;

import gui.FormGuiBuilder;
import io.JsonReader;
import validation.FormDefinitionValidator;

public class FormController {
    private final JsonReader jsonReader;
    private final FormDefinitionValidator validator;
    private FormGuiBuilder guiBuilder;

    public FormController(JsonReader jsonReader, FormDefinitionValidator validator, FormGuiBuilder guiBuilder) {
        this.jsonReader = jsonReader;
        this.validator = validator;
        this.guiBuilder = guiBuilder;
    }
}
