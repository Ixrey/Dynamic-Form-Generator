package io;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.FormDefinition;

public class JsonReader {
    private final ObjectMapper mapper = new ObjectMapper();

    public FormDefinition mapToFormDefinition(JsonNode root) throws IOException {
        return mapper.treeToValue(root, FormDefinition.class);
    }

    public JsonNode readRaw(File file) throws IOException {
        return mapper.readTree(file);
    }
}
