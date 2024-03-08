package ru.javawebinar.topjava;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import ru.javawebinar.topjava.web.json.JacksonObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class TestJsonUtil {

    public static List<String> readExceptionsFromJson(String json, String key) throws JsonProcessingException {
        JsonNode keyNode = JacksonObjectMapper.getMapper().readTree(json).get(key);
        List<String> values = new ArrayList<>();
        for (JsonNode node : keyNode) {
            values.add(node.asText());
        }
        return values;
    }
}
