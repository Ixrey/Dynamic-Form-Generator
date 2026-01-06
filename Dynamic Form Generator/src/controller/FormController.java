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
import validation.FormInputValidator;

public class FormController {
    private final JsonReader jsonReader;
    private final FormDefinitionValidator formValidator;
    private final FormInputValidator inputValidator;
    private final MainWindow mainWindow;
    private final FormGuiBuilder guiBuilder;
    private final JsonWriter jsonWriter;
    private FormDefinition currentFormDefinition;
    private File currentFormFile;

    public FormController(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.jsonReader = new JsonReader();
        this.formValidator = new FormDefinitionValidator();
        this.inputValidator = new FormInputValidator();
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

            loadFormFromFile(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFormFromFile(File file) throws IOException {

        JsonNode node = jsonReader.readRaw(file);
        List<String> errors = formValidator.validate(node);

        if (errors.isEmpty()) {
            currentFormDefinition = jsonReader.mapToFormDefinition(node);
            currentFormFile = file;
            JPanel panel = guiBuilder.buildFormPanel(currentFormDefinition);
            mainWindow.showFormPanel(panel);
        } else {
            mainWindow.showValidationErrors(errors);
        }
    }

    public void handleSaveResult() {
        if (currentFormDefinition == null) {
            mainWindow.showErrorMessage("Bitte laden sie zuerst ein Formular");
            return;
        }

        Map<String, Object> values = guiBuilder.getCurrentValues();

        guiBuilder.clearValidationMarks();

        Map<String, String> errors = inputValidator.validate(currentFormDefinition, values);

        if (!errors.isEmpty()) {
            for (Map.Entry<String, String> entry : errors.entrySet()) {
                String fieldId = entry.getKey();
                String errorMessage = entry.getValue();
                guiBuilder.markFieldInvalid(fieldId, errorMessage);
            }

            mainWindow.showValidationErrors(errors.values());
            return;
        }

        FormResult result = new FormResult();
        result.setFormTitle(currentFormDefinition.getFormTitle());
        result.setSubmittedAt(LocalDateTime.now().toString());
        result.setValues(values);

        if (currentFormFile != null) {
            result.setFormDefinitionPath(currentFormFile.getAbsolutePath());
        }

        File file = mainWindow.chooseResultFileToSave();

        if (file == null) {
            return;
        }

        try {
            jsonWriter.writeFormResult(result, file);
            mainWindow.showInfoMessage("Formular erfolgreich gespeichert!");
            JPanel newPanel = guiBuilder.buildFormPanel(currentFormDefinition);
            mainWindow.showFormPanel(newPanel);
        } catch (IOException e) {
            mainWindow.showErrorMessage("Das Formular konnte nicht gespeichert werden.");
            e.printStackTrace();
        }
    }

    public void handleLoadResult() {
        File file = mainWindow.chooseResultFileToOpen();

        if (file == null) {
            return;
        }

        try {
            JsonNode node = jsonReader.readRaw(file);
            FormResult result = jsonReader.mapToFormResult(node);

            String formPath = result.getFormDefinitionPath();

            if (formPath == null || formPath.isBlank()) {
                mainWindow
                        .showErrorMessage("In der Ergebnisdatei ist kein Verweis auf die Formulardatei gespeichert.\n" +
                                "Das Ergebnis kann ohne passende Formulardefinition nicht geladen werden");
                return;
            }

            File formFile = new File(formPath);
            if (!formFile.exists()) {
                mainWindow.showErrorMessage(
                        "Es konnte keine passende Formulardatei für \"" + formPath + "\" gefunden werden.\n"
                                + "Erwartete Datei: " + formFile.getAbsolutePath());
                return;
            }

            loadFormFromFile(formFile);

            if (currentFormDefinition == null) {
                mainWindow.showErrorMessage("Das zugehörige Formular konnte nicht korrekt geladen werden.");
                return;
            }

            Map<String, String> errors = inputValidator.validate(currentFormDefinition, result.getValues());

            if (!errors.isEmpty()) {
                mainWindow.showValidationErrors(errors.values());
                return;
            }

            guiBuilder.applyValues(result.getValues());

            mainWindow.showInfoMessage("Ergebnisdaten wurden in das Formular geladen.");
        } catch (IOException e) {
            e.printStackTrace();
            mainWindow.showErrorMessage("Beim Laden der Ergebnis-Datei ist ein Fehler aufgetreten.");
        }
    }

}
