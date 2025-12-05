package model;

import java.util.Map;

public class FormResult {
    private String formTitle;
    private String submittedAt;
    private Map<String, Object> values;
    private String formDefinitionPath;

    public String getFormTitle() {
        return formTitle;
    }

    public void setFormTitle(String formTitle) {
        this.formTitle = formTitle;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    public String getFormDefinitionPath() {
        return formDefinitionPath;
    }

    public void setFormDefinitionPath(String formDefinitionPath) {
        this.formDefinitionPath = formDefinitionPath;
    }
}
