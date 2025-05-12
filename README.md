# Clojure LLM Chat Interface

A simple command-line chat interface for interacting with local LLM servers using Clojure and Leiningen.

## Prerequisites

1. [Leiningen](https://leiningen.org/) installed
2. A running LLM server that supports the OpenAI API format (like LM Studio)

## Getting Started

1. Clone or download this repository
2. Navigate to the project directory
3. Run the application using Leiningen

```bash
lein run
```

## Command Line Options

You can customize the behavior using the following options:

```
  -u, --url URL        API Base URL (default: "http://localhost:1234/v1")
  -k, --api-key KEY    API Key (default: "lm-studio")
  -m, --model MODEL    Model name (default: "TheBloke/dolphin-2.2.1-mistral-7B-GGUF")
  -t, --temperature TEMP  Temperature (default: 1.1)
  -l, --max-tokens TOKENS  Max tokens (default: 140)
  -h, --help
```

Example with custom options:

```bash
lein run -u "http://localhost:8000/v1" -k "your-api-key" -m "another-model" -t 0.7 -l 200
```

## Building a Standalone JAR

To create a standalone executable JAR:

```bash
lein uberjar
```

Then run it with:

```bash
java -jar target/uberjar/llm-chat-0.1.0-SNAPSHOT-standalone.jar
```

## How It Works

This application:

1. Establishes a connection to a local LLM server using the OpenAI API format
2. Maintains a conversation history
3. Sends user input to the LLM server
4. Displays the LLM's responses

The implementation uses:

- clj-http for making HTTP requests
- cheshire for JSON parsing
- tools.cli for command-line argument processing

## Project Structure

- `project.clj` - Project configuration and dependencies
- `src/llm_chat/core.clj` - Main application code