# Tool Server with gRPC Support

A tool server with gRPC support that implements two tools:
1. `hello_world` - A simple greeting tool
2. `calculator` - A tool that performs basic arithmetic operations

## Building the Project

```bash
mvn clean package
```

This will create a runnable JAR file in the `target` directory.

## Running the Server

The server runs on port 50051 by default. You can specify a different port as a command-line argument:

```bash
# Run on default port (50051)
java -jar target/simple-tool-server-1.0-SNAPSHOT.jar

# Run on a specific port
java -jar target/simple-tool-server-1.0-SNAPSHOT.jar 8080
```

## Available Tools

### hello_world

A simple tool that returns a greeting message.

**Parameters:**
- `name` (optional): The name to greet. Defaults to "World" if not provided.

**Example:**
```json
{
  "jsonrpc": "2.0",
  "id": "request-id",
  "method": "execute_tool",
  "params": {
    "tool_name": "hello_world",
    "parameters": {
      "name": "Claude"
    }
  }
}
```

**Response:**
```json
{
  "jsonrpc": "2.0",
  "id": "request-id",
  "result": {
    "message": "Hello, Claude!"
  }
}
```

### calculator

A tool that performs basic arithmetic operations.

**Parameters:**
- `operation` (required): The operation to perform. Must be one of: "add", "subtract", "multiply", "divide"
- `a` (required): The first number
- `b` (required): The second number

**Example:**
```json
{
  "jsonrpc": "2.0",
  "id": "request-id",
  "method": "execute_tool",
  "params": {
    "tool_name": "calculator",
    "parameters": {
      "operation": "add",
      "a": "5",
      "b": "3"
    }
  }
}
```

**Response:**
```json
{
  "jsonrpc": "2.0",
  "id": "request-id",
  "result": {
    "result": 8.0
  }
}
```

## Interacting with the Server

You can interact with the server using the included gRPC client:

```bash
# Compile and run the client
mvn compile exec:java -Dexec.mainClass="com.example.mcpserver.client.GrpcToolClient"

# Specify a different host or port
mvn compile exec:java -Dexec.mainClass="com.example.mcpserver.client.GrpcToolClient" -Dexec.args="localhost 8080"
```

The client will automatically:
1. List all available tools
2. Test the hello_world tool
3. Test the calculator tool with different operations

## Using with Custom gRPC Clients

The server exposes a gRPC service defined in `src/main/proto/toolservice.proto`. You can generate client code in any language supported by gRPC.

### ListTools Method

```protobuf
rpc ListTools (ListToolsRequest) returns (ListToolsResponse) {}
```

### ExecuteTool Method

```protobuf
rpc ExecuteTool (ExecuteToolRequest) returns (ExecuteToolResponse) {}
```

## Extending the Server

To add a new tool:

1. Create a new class that implements the `Tool` interface in the `com.example.mcpserver.tools` package
2. Implement the required methods: `getName()`, `getDescription()`, and `execute()`
3. Register your tool in the `GrpcToolServer` class

Example implementation for a new tool:

```java
public class MyNewTool implements Tool {
    @Override
    public String getName() {
        return "my_new_tool";
    }

    @Override
    public String getDescription() {
        return "Description of what my new tool does";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> parameters) {
        // Implement your tool's logic here
        Map<String, Object> result = new HashMap<>();
        // Process parameters and add results
        result.put("key", "value");
        return result;
    }
}
```

Then register it in the `GrpcToolServer` class:

```java
// In GrpcToolServer constructor
registerTool(new MyNewTool());
``` 