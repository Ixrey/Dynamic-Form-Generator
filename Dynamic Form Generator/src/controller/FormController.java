package controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;

import gui.FormGuiBuilder;
import gui.MainWindow;
import io.JsonReader;
import io.JsonWriter;
import model.FormDefinition;
import model.FormResult;
import validation.FormDefinitionValidator;

public class FormController {
    private final JsonReader jsonReader;
    private final FormDefinitionValidator validator;
    private final MainWindow mainWindow;
    private final FormGuiBuilder guiBuilder;
    private final JsonWriter jsonWriter;
    private FormDefinition currentFormDefinition;

    // private File file = new File("forms/Kunden-Feedback.json");

    public FormController(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.jsonReader = new JsonReader();
        this.validator = new FormDefinitionValidator();
        this.guiBuilder = new FormGuiBuilder();
        this.jsonWriter = new JsonWriter();
        registerListener();
    }

    public void registerListener() {
        mainWindow.addLoadFormListener(e -> handleLoadForm());
        mainWindow.addLoadResultListener(e -> handleLoadResult());
        mainWindow.addSaveResultListener(e -> handleSaveResult());
    }

    public void handleLoadForm() {
        try {
            File file = mainWindow.chooseFormToOpen();

            if (file == null) {
                return;
            }

            JsonNode node = jsonReader.readRaw(file);
            List<String> errors = validator.validate(node);

            if (errors.isEmpty()) {
                currentFormDefinition = jsonReader.mapToFormDefinition(node);
                JPanel panel = guiBuilder.buildFormPanel(currentFormDefinition);
                mainWindow.showFormPanel(panel);
            } else {
                mainWindow.showValidationErrors(errors);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSaveResult() {
        if (currentFormDefinition == null) {
            mainWindow.showErrorMessage("Bitte laden sie zuerst ein Formular");
            return;
        }

        Map<String, Object> values = guiBuilder.getCurrentValues();

        FormResult result = new FormResult();
        result.setFormTitle(currentFormDefinition.getFormTitle());
        result.setSubmittedAt(LocalDateTime.now().toString());
        result.setValues(values);

        File file = mainWindow.chooseResultFileToSave();

        if (file == null) {
            return;
        }

        try {
            jsonWriter.writeFormResult(result, file);
            mainWindow.showInfoMessage("Formular erfolgreich gespeichert!");
        } catch (IOException e) {
            mainWindow.showErrorMessage("Das Formular konnte nicht gespeichert werden.");
            e.printStackTrace();
        }
    }

    public void handleLoadResult() {
        if (currentFormDefinition == null) {
            mainWindow.showErrorMessage("Bitte laden Sie zuerst ein Formular, bevor Ergebnisdaten geladen werden.");
            return;
        }

        File file = mainWindow.chooseResultFileToOpen();

        if (file == null) {
            return;
        }

        try {
            JsonNode node = jsonReader.readRaw(file);
            FormResult result = jsonReader.mapToFormResult(node);
            guiBuilder.applyValues(result.getValues());
            mainWindow.showInfoMessage("Ergebnisdaten wurden in das Formular geladen.");
        } catch (IOException e) {
            e.printStackTrace();
            mainWindow.showErrorMessage("Beim Laden der Ergebnis-Datei ist ein Fehler aufgetreten.");
        }
    }

}
