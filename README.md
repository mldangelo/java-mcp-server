# Simple Tool Server

A simple JSON-RPC based tool server that implements two tools:
1. `hello_world` - A simple greeting tool
2. `calculator` - A tool that performs basic arithmetic operations

## Building the Project

```bash
mvn clean package
```

This will create a runnable JAR file in the `target` directory.

## Running the Server

```bash
java -jar target/simple-tool-server-1.0-SNAPSHOT.jar
```

The server listens on STDIN and writes responses to STDOUT, making it suitable for integration with other processes via pipes.

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

## Listing Available Tools

To get a list of all available tools:

```json
{
  "jsonrpc": "2.0",
  "id": "request-id",
  "method": "list_tools",
  "params": {}
}
```

**Response:**
```json
{
  "jsonrpc": "2.0",
  "id": "request-id",
  "result": {
    "tools": {
      "calculator": {
        "description": "A tool that performs basic arithmetic operations (add, subtract, multiply, divide)"
      },
      "hello_world": {
        "description": "A simple tool that returns a greeting message"
      }
    }
  }
}
```

## Using the Server

### Using Pipes

You can pipe JSON-RPC requests to the server:

```bash
echo '{"jsonrpc": "2.0", "id": "1", "method": "list_tools", "params": {}}' | java -jar target/simple-tool-server-1.0-SNAPSHOT.jar
```

### Interactive Mode

For interactive use, you can run the server and then type JSON-RPC requests line by line:

```bash
java -jar target/simple-tool-server-1.0-SNAPSHOT.jar
```

Then in the terminal, enter each JSON-RPC request on a single line.

### Using with Script Files

You can prepare a file with multiple JSON-RPC requests (one per line) and use it as input:

```bash
java -jar target/simple-tool-server-1.0-SNAPSHOT.jar < requests.json
```

## Detailed Testing Instructions

Here are some examples of testing the server with different requests:

### Testing Tool Listing

```bash
echo '{"jsonrpc": "2.0", "id": "1", "method": "list_tools", "params": {}}' | java -jar target/simple-tool-server-1.0-SNAPSHOT.jar
```

Expected output should include both `hello_world` and `calculator` tools.

### Testing Hello World Tool

1. Basic greeting:
```bash
echo '{"jsonrpc": "2.0", "id": "2", "method": "execute_tool", "params": {"tool_name": "hello_world"}}' | java -jar target/simple-tool-server-1.0-SNAPSHOT.jar
```

2. Greeting with a name:
```bash
echo '{"jsonrpc": "2.0", "id": "3", "method": "execute_tool", "params": {"tool_name": "hello_world", "parameters": {"name": "Alice"}}}' | java -jar target/simple-tool-server-1.0-SNAPSHOT.jar
```

### Testing Calculator Tool

1. Addition:
```bash
echo '{"jsonrpc": "2.0", "id": "4", "method": "execute_tool", "params": {"tool_name": "calculator", "parameters": {"operation": "add", "a": "10", "b": "5"}}}' | java -jar target/simple-tool-server-1.0-SNAPSHOT.jar
```

2. Subtraction:
```bash
echo '{"jsonrpc": "2.0", "id": "5", "method": "execute_tool", "params": {"tool_name": "calculator", "parameters": {"operation": "subtract", "a": "10", "b": "5"}}}' | java -jar target/simple-tool-server-1.0-SNAPSHOT.jar
```

3. Multiplication:
```bash
echo '{"jsonrpc": "2.0", "id": "6", "method": "execute_tool", "params": {"tool_name": "calculator", "parameters": {"operation": "multiply", "a": "10", "b": "5"}}}' | java -jar target/simple-tool-server-1.0-SNAPSHOT.jar
```

4. Division:
```bash
echo '{"jsonrpc": "2.0", "id": "7", "method": "execute_tool", "params": {"tool_name": "calculator", "parameters": {"operation": "divide", "a": "10", "b": "5"}}}' | java -jar target/simple-tool-server-1.0-SNAPSHOT.jar
```

5. Division by zero (should return an error):
```bash
echo '{"jsonrpc": "2.0", "id": "8", "method": "execute_tool", "params": {"tool_name": "calculator", "parameters": {"operation": "divide", "a": "10", "b": "0"}}}' | java -jar target/simple-tool-server-1.0-SNAPSHOT.jar
```

### Testing Error Handling

1. Unknown tool:
```bash
echo '{"jsonrpc": "2.0", "id": "9", "method": "execute_tool", "params": {"tool_name": "unknown_tool"}}' | java -jar target/simple-tool-server-1.0-SNAPSHOT.jar
```

2. Missing required parameters:
```bash
echo '{"jsonrpc": "2.0", "id": "10", "method": "execute_tool", "params": {"tool_name": "calculator", "parameters": {"a": "10", "b": "5"}}}' | java -jar target/simple-tool-server-1.0-SNAPSHOT.jar
```

## Extending the Server

To add a new tool:

1. Create a new class that implements the `Tool` interface in the `com.example.mcpserver.tools` package
2. Implement the required methods: `getName()`, `getDescription()`, and `execute()`
3. Register your tool in the `SimpleToolServer` class by adding it to the list of tools

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

Then register it in the `SimpleToolServer` class:

```java
registerTool(new MyNewTool());
``` 