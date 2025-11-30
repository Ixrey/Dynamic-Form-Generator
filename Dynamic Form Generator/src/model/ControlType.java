package model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ControlType {
    TEXTFIELD,
    TEXTAREA,
    CHECKBOX,
    DROPDOWN,
    DATE,
    SPINNER;

    @JsonCreator
    public static ControlType fromJson(String value) {
        return ControlType.valueOf(value.toUpperCase());
    }
}
