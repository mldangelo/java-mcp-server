package com.example.mcpserver.model;

import java.util.Map;

/**
 * Interface for tools that can be executed by the server
 */
public interface Tool {
    /**
     * Get the name of the tool
     * @return the tool name
     */
    String getName();
    
    /**
     * Get the description of the tool
     * @return the tool description
     */
    String getDescription();
    
    /**
     * Execute the tool with the given parameters
     * @param parameters the parameters for the tool execution
     * @return the result of the tool execution
     */
    Map<String, Object> execute(Map<String, Object> parameters);
} 