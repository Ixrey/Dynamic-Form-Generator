package model;

import java.util.List;

public class FormDefinition {
    private String formTitle;
    private List<FormField> fields;

    public FormDefinition() {
    }

    public String getFormTitle() {
        return formTitle;
    }

    public void setFormTitle(String formTitle) {
        this.formTitle = formTitle;
    }

    public List<FormField> getFields() {
        return fields;
    }

    public void setFields(List<FormField> fields) {
        this.fields = fields;
    }

}