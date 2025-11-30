package model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DataType {
    STRING,
    INT,
    FLOAT,
    BOOLEAN,
    DATE;

    @JsonCreator
    public static DataType fromJson(String value) {
        return DataType.valueOf(value.toUpperCase());
    }
}
