package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

public class JacksonTest {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        var node = mapper.readTree(new File("test.json"));
        System.out.println(node);
    }
}
