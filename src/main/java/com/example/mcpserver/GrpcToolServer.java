package com.example.mcpserver;

import com.example.mcpserver.model.Tool;
import com.example.mcpserver.service.ToolServiceImpl;
import com.example.mcpserver.tools.CustomCalculatorTool;
import com.example.mcpserver.tools.CustomHelloWorldTool;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * gRPC Server for hosting tools
 */
public class GrpcToolServer {
    
    private static final int DEFAULT_PORT = 50051;
    private final Server server;
    private final int port;
    private final Map<String, Tool> tools = new HashMap<>();
    
    public GrpcToolServer(int port) {
        this.port = port;
        
        // Register tools
        registerTool(new CustomHelloWorldTool());
        registerTool(new CustomCalculatorTool());
        
        // Create the gRPC server
        this.server = ServerBuilder.forPort(port)
                .addService(new ToolServiceImpl(tools))
                .build();
    }
    
    /**
     * Register a tool with the server
     */
    private void registerTool(Tool tool) {
        tools.put(tool.getName(), tool);
    }
    
    /**
     * Start the server
     */
    public void start() throws IOException {
        server.start();
        System.out.println("gRPC Tool Server started on port " + port);
        System.out.println("Available tools: " + String.join(", ", tools.keySet()));
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server...");
            try {
                GrpcToolServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.out.println("Server shut down");
        }));
    }
    
    /**
     * Stop the server
     */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
    
    /**
     * Block until server is terminated
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
    
    /**
     * Main method to start the server
     */
    public static void main(String[] args) throws Exception {
        // Determine the port to use
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Usage: [port]");
                System.err.println("Using default port: " + DEFAULT_PORT);
            }
        }
        
        // Create and start the server
        final GrpcToolServer server = new GrpcToolServer(port);
        server.start();
        
        // Keep the application running
        server.blockUntilShutdown();
    }
} 