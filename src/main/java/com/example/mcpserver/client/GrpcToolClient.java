package com.example.mcpserver.client;

import com.example.mcpserver.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A simple client for the gRPC Tool Server
 */
public class GrpcToolClient {
    
    private final ToolServiceGrpc.ToolServiceBlockingStub blockingStub;
    private final ManagedChannel channel;
    
    /**
     * Constructor for the client
     */
    public GrpcToolClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext() // No TLS for simplicity in this example
                .build();
        this.blockingStub = ToolServiceGrpc.newBlockingStub(channel);
    }
    
    /**
     * Shutdown the channel
     */
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
    
    /**
     * List all available tools
     */
    public Map<String, String> listTools() {
        System.out.println("Listing tools...");
        ListToolsRequest request = ListToolsRequest.newBuilder().build();
        ListToolsResponse response;
        
        try {
            response = blockingStub.listTools(request);
        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
            return new HashMap<>();
        }
        
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, ToolInfo> entry : response.getToolsMap().entrySet()) {
            result.put(entry.getKey(), entry.getValue().getDescription());
        }
        
        return result;
    }
    
    /**
     * Execute a tool
     */
    public Map<String, String> executeTool(String toolName, Map<String, String> parameters) {
        System.out.println("Executing tool: " + toolName);
        
        ExecuteToolRequest.Builder requestBuilder = ExecuteToolRequest.newBuilder()
                .setToolName(toolName);
        
        // Add parameters to the request
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            requestBuilder.putParameters(entry.getKey(), entry.getValue());
        }
        
        ExecuteToolResponse response;
        try {
            response = blockingStub.executeTool(requestBuilder.build());
        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
            return Map.of("error", "RPC failed: " + e.getStatus());
        }
        
        // Check for errors
        if (response.hasError()) {
            return Map.of("error", response.getError().getMessage());
        }
        
        // Return the success result
        return new HashMap<>(response.getSuccess().getValuesMap());
    }
    
    /**
     * Main method to run the client
     */
    public static void main(String[] args) throws Exception {
        // Set default parameters
        String host = "localhost";
        int port = 50051;
        
        // Parse command-line arguments
        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }
        
        // Create and run the client
        GrpcToolClient client = new GrpcToolClient(host, port);
        
        try {
            // List tools
            Map<String, String> tools = client.listTools();
            System.out.println("Available tools:");
            for (Map.Entry<String, String> entry : tools.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
            
            // Test hello_world tool
            Map<String, String> helloParams = new HashMap<>();
            helloParams.put("name", "gRPC");
            Map<String, String> helloResult = client.executeTool("hello_world", helloParams);
            System.out.println("Hello World Result: " + helloResult);
            
            // Test calculator tool (addition)
            Map<String, String> calcParams = new HashMap<>();
            calcParams.put("operation", "add");
            calcParams.put("a", "10");
            calcParams.put("b", "5");
            Map<String, String> calcResult = client.executeTool("calculator", calcParams);
            System.out.println("Calculator Result (add): " + calcResult);
            
            // Test calculator tool (subtraction)
            calcParams.put("operation", "subtract");
            calcResult = client.executeTool("calculator", calcParams);
            System.out.println("Calculator Result (subtract): " + calcResult);
            
            // Test calculator tool (multiplication)
            calcParams.put("operation", "multiply");
            calcResult = client.executeTool("calculator", calcParams);
            System.out.println("Calculator Result (multiply): " + calcResult);
            
            // Test calculator tool (division)
            calcParams.put("operation", "divide");
            calcResult = client.executeTool("calculator", calcParams);
            System.out.println("Calculator Result (divide): " + calcResult);
            
        } finally {
            client.shutdown();
        }
    }
} 