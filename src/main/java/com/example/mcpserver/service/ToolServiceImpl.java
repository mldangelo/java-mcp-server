package com.example.mcpserver.service;

import com.example.mcpserver.grpc.*;
import com.example.mcpserver.model.Tool;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the gRPC ToolService
 */
public class ToolServiceImpl extends ToolServiceGrpc.ToolServiceImplBase {
    
    private final Map<String, Tool> tools;
    
    public ToolServiceImpl(Map<String, Tool> tools) {
        this.tools = tools;
    }
    
    @Override
    public void listTools(ListToolsRequest request, StreamObserver<ListToolsResponse> responseObserver) {
        ListToolsResponse.Builder responseBuilder = ListToolsResponse.newBuilder();
        
        // Add all available tools to the response
        for (Tool tool : tools.values()) {
            ToolInfo toolInfo = ToolInfo.newBuilder()
                    .setDescription(tool.getDescription())
                    .build();
            
            responseBuilder.putTools(tool.getName(), toolInfo);
        }
        
        // Send the response
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
    
    @Override
    public void executeTool(ExecuteToolRequest request, StreamObserver<ExecuteToolResponse> responseObserver) {
        ExecuteToolResponse.Builder responseBuilder = ExecuteToolResponse.newBuilder();
        
        String toolName = request.getToolName();
        
        // Check if the requested tool exists
        if (!tools.containsKey(toolName)) {
            ErrorInfo error = ErrorInfo.newBuilder()
                    .setMessage("Tool not found: " + toolName)
                    .build();
            
            responseBuilder.setError(error);
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            return;
        }
        
        try {
            // Get the tool and execute it
            Tool tool = tools.get(toolName);
            
            // Convert parameters map
            Map<String, Object> params = new HashMap<>();
            for (Map.Entry<String, String> entry : request.getParametersMap().entrySet()) {
                params.put(entry.getKey(), entry.getValue());
            }
            
            // Execute the tool
            Map<String, Object> result = tool.execute(params);
            
            // Build the success response
            ToolResult.Builder resultBuilder = ToolResult.newBuilder();
            
            // Convert result values to strings
            for (Map.Entry<String, Object> entry : result.entrySet()) {
                resultBuilder.putValues(entry.getKey(), String.valueOf(entry.getValue()));
            }
            
            responseBuilder.setSuccess(resultBuilder.build());
            
        } catch (Exception e) {
            // Handle any errors during execution
            ErrorInfo error = ErrorInfo.newBuilder()
                    .setMessage("Error executing tool: " + e.getMessage())
                    .build();
            
            responseBuilder.setError(error);
        }
        
        // Send the response
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
} 