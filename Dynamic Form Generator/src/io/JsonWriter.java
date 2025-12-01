package io;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.FormResult;

public class JsonWriter {
    private final ObjectMapper mapper = new ObjectMapper();

    public void writeFormResult(FormResult result, File file) throws IOException {
        mapper.writeValue(file, result);
    }
}
