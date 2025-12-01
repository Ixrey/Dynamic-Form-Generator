package controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;

import gui.FormGuiBuilder;
import gui.MainWindow;
import io.JsonReader;
import model.FormDefinition;
import validation.FormDefinitionValidator;

public class FormController {
    private final JsonReader jsonReader;
    private final FormDefinitionValidator validator;
    private final MainWindow mainWindow;
    private final FormGuiBuilder guiBuilder;

    private File file = new File("forms/Kunden-Feedback.json");

    public FormController(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.jsonReader = new JsonReader();
        this.validator = new FormDefinitionValidator();
        this.guiBuilder = new FormGuiBuilder();
        registerListener();
    }

    public void registerListener() {
        mainWindow.addLoadFormListener(e -> handleLoadForm());
        mainWindow.addLoadResultListener(e -> handleLoadResult());
        mainWindow.addSaveResultListener(e -> handleSaveResult());
    }

    public void handleLoadForm() {
        try {
            JsonNode node = jsonReader.readRaw(file);
            List<String> errors = validator.validate(node);
            if (errors.isEmpty()) {
                FormDefinition def = jsonReader.mapToFormDefinition(node);
                JPanel panel = guiBuilder.buildFormPanel(def);
                mainWindow.showFormPanel(panel);
            } else {
                mainWindow.showValidationErrors(errors);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleLoadResult() {

    }

    public void handleSaveResult() {

    }
}
