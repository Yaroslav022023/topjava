package ru.javawebinar.topjava;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import java.util.List;

public class TestJsonUtil {

    public static List<String> readExceptionsFromJson(String json, String key) throws JsonProcessingException {
        ReadContext ctx = JsonPath.parse(json);
        return ctx.read("$." + key, List.class);
    }
}
