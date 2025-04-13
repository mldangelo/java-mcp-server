package com.example.mcpserver.tools;

import com.example.mcpserver.model.Tool;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple tool that returns a hello message
 */
public class CustomHelloWorldTool implements Tool {

    @Override
    public String getName() {
        return "hello_world";
    }

    @Override
    public String getDescription() {
        return "A simple tool that returns a greeting message";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> parameters) {
        String name = parameters.containsKey("name") ? parameters.get("name").toString() : "World";
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello, " + name + "!");
        return result;
    }
} 