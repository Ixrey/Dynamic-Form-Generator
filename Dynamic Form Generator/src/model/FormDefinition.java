package model;

import java.util.List;

public class FormDefinition {
    private String formTitle;
    private List<FieldDefinition> fields;

    public FormDefinition(String formTitle, List<FieldDefinition> fields) {
        this.formTitle = formTitle;
        this.fields = fields;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public void setFormTitle(String formTitle) {
        this.formTitle = formTitle;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public void setFields(List<FieldDefinition> fields) {
        this.fields = fields;
    }

}