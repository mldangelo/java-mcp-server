package com.example.mcpserver.tools;

import com.example.mcpserver.model.Tool;

import java.util.HashMap;
import java.util.Map;

/**
 * A tool that performs basic arithmetic operations
 */
public class CustomCalculatorTool implements Tool {

    @Override
    public String getName() {
        return "calculator";
    }

    @Override
    public String getDescription() {
        return "A tool that performs basic arithmetic operations (add, subtract, multiply, divide)";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> parameters) {
        Map<String, Object> result = new HashMap<>();
        
        if (!parameters.containsKey("operation")) {
            result.put("error", "Missing required parameter: operation");
            return result;
        }
        
        if (!parameters.containsKey("a")) {
            result.put("error", "Missing required parameter: a");
            return result;
        }
        
        if (!parameters.containsKey("b")) {
            result.put("error", "Missing required parameter: b");
            return result;
        }
        
        String operation = parameters.get("operation").toString();
        
        try {
            double a = Double.parseDouble(parameters.get("a").toString());
            double b = Double.parseDouble(parameters.get("b").toString());
            
            double calculationResult;
            
            switch (operation) {
                case "add":
                    calculationResult = a + b;
                    break;
                case "subtract":
                    calculationResult = a - b;
                    break;
                case "multiply":
                    calculationResult = a * b;
                    break;
                case "divide":
                    if (b == 0) {
                        result.put("error", "Cannot divide by zero");
                        return result;
                    }
                    calculationResult = a / b;
                    break;
                default:
                    result.put("error", "Unsupported operation: " + operation);
                    return result;
            }
            
            result.put("result", calculationResult);
            
        } catch (NumberFormatException e) {
            result.put("error", "Invalid number format: " + e.getMessage());
        }
        
        return result;
    }
} 