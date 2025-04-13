package com.example.mcpserver;

import com.example.mcpserver.model.Tool;
import com.example.mcpserver.tools.CustomCalculatorTool;
import com.example.mcpserver.tools.CustomHelloWorldTool;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimpleToolServer {
    private static final Gson gson = new Gson();
    private static final Map<String, Tool> tools = new HashMap<>();

    public static void main(String[] args) {
        // Register tools
        registerTool(new CustomHelloWorldTool());
        registerTool(new CustomCalculatorTool());
        
        System.err.println("Simple Tool Server started with tools: hello_world, calculator");
        
        // Start handling requests
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                handleRequest(line);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void registerTool(Tool tool) {
        tools.put(tool.getName(), tool);
    }
    
    private static void handleRequest(String requestJson) {
        try {
            JsonObject request = gson.fromJson(requestJson, JsonObject.class);
            
            if (request.has("method") && request.has("params")) {
                String method = request.get("method").getAsString();
                JsonObject params = request.getAsJsonObject("params");
                
                if ("execute_tool".equals(method) && params.has("tool_name")) {
                    String toolName = params.get("tool_name").getAsString();
                    JsonObject toolParams = params.has("parameters") ? 
                            params.getAsJsonObject("parameters") : new JsonObject();
                    
                    executeTool(request.get("id").getAsString(), toolName, toolParams);
                } else if ("list_tools".equals(method)) {
                    listTools(request.get("id").getAsString());
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing request: " + e.getMessage());
        }
    }
    
    private static void executeTool(String requestId, String toolName, JsonObject toolParams) {
        JsonObject response = new JsonObject();
        response.addProperty("jsonrpc", "2.0");
        response.addProperty("id", requestId);
        
        if (tools.containsKey(toolName)) {
            Tool tool = tools.get(toolName);
            
            // Convert JsonObject to Map
            Map<String, Object> paramsMap = new HashMap<>();
            toolParams.keySet().forEach(key -> paramsMap.put(key, toolParams.get(key).getAsString()));
            
            try {
                Map<String, Object> result = tool.execute(paramsMap);
                response.add("result", gson.toJsonTree(result));
            } catch (Exception e) {
                JsonObject error = new JsonObject();
                error.addProperty("code", -32000);
                error.addProperty("message", "Tool execution error: " + e.getMessage());
                response.add("error", error);
            }
        } else {
            JsonObject error = new JsonObject();
            error.addProperty("code", -32602);
            error.addProperty("message", "Tool not found: " + toolName);
            response.add("error", error);
        }
        
        System.out.println(gson.toJson(response));
    }
    
    private static void listTools(String requestId) {
        JsonObject response = new JsonObject();
        response.addProperty("jsonrpc", "2.0");
        response.addProperty("id", requestId);
        
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> toolsMap = new HashMap<>();
        for (Tool tool : tools.values()) {
            Map<String, Object> toolInfo = new HashMap<>();
            toolInfo.put("description", tool.getDescription());
            toolsMap.put(tool.getName(), toolInfo);
        }
        
        result.put("tools", toolsMap);
        response.add("result", gson.toJsonTree(result));
        
        System.out.println(gson.toJson(response));
    }
} 