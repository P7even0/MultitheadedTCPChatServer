# Multithreaded TCP Chat Server

A simple multithreaded TCP chat server built in **Java**, allowing multiple clients to connect and exchange messages in real time.

The project uses Java's built-in socket API to handle TCP connections, with a separate thread for each connected client. Messages are broadcast to all connected clients, and synchronization is used to protect shared resources.

This project was created as a networking and concurrency learning exercise.

## Features

- **Multiple client connections:** Supports multiple clients connected simultaneously.
- **Multithreading:** Each client is handled independently using a dedicated thread.
- **Message broadcasting:** Messages received from one client are forwarded to all connected clients, including the sender.
- **Automatic client identification:** Each client receives a unique numeric identifier, such as `Client 0` or `Client 1`.
- **Thread safety:** Uses synchronization to protect the shared client collection and client ID counter.
- **Disconnection handling:** Removes disconnected clients from the shared collection and closes their sockets.
- **Automated integration tests:** Includes tests for message integrity, concurrent broadcasting, disconnections, and repeated connections.

## Technologies Used

- Java
- Java Sockets (`ServerSocket`, `Socket`)
- Java Threads (`Thread`, `Runnable`)
- Java I/O (`BufferedReader`, `PrintWriter`)
- Java Collections (`HashMap`)
- Java Synchronization (`synchronized`)
- PowerShell (for manual connection testing)

No external libraries or frameworks are required.

## How It Works

The server listens for incoming TCP connections on port `5000`.

When a client connects:

1. The main thread accepts the connection using `ServerSocket.accept()`.
2. A new `ClientHandler` instance is created and executed in a separate thread.
3. The client receives a unique identifier and is registered in a shared `HashMap`.
4. The handler continuously reads incoming messages using `BufferedReader.readLine()`.
5. Received messages are broadcast to all connected clients through their `PrintWriter` instances.
6. When a client disconnects, its connection is closed and its entry is removed from the map.

### Server Architecture

```text
                    TCP Server
                     Port 5000
                         |
                    ServerSocket
                         |
                  Main Thread
                         |
             Accepts New Connections
                         |
             +-----------+-----------+
             |           |           |
          Thread 0    Thread 1    Thread 2
             |           |           |
          Client 0    Client 1    Client 2
             |           |           |
             +-----------+-----------+
                         |
                 Shared Client Map
                         |
                  Message Broadcast
```

### Synchronization

Connected clients are stored in a shared `HashMap<String, PrintWriter>`.

Because multiple threads can access this collection simultaneously, the server uses a shared static lock to synchronize:

- Client registration
- Client ID generation
- Message broadcasting
- Client removal

Blocking reads are performed outside synchronized blocks, allowing client handlers to wait for incoming messages independently.

Broadcasting is synchronized to prevent concurrent modifications to the client map and coordinate writes to connected clients.

## Requirements

- JDK 17 or later
- A terminal or TCP client such as Telnet

The project has been developed and tested on Windows.

## How to Run

### 1. Start the Server

Compile the Java source file:

```bash
javac Main.java
```

Run the server:

```bash
java Main
```

The server starts listening on port `5000`.

### 2. Connect Clients

Open a separate terminal for each client.

Using Telnet:

```bash
telnet localhost 5000
```

Repeat this command in additional terminals to connect multiple clients.

### 3. Send Messages

Type a message and press Enter.

Messages are forwarded to every connected client.

Example:

```text
Client 0: Hello everyone!
Client 1: Hey!
Client 2: What's up?
```

Each message is prefixed with the sender's automatically assigned identifier.

The server uses newline-delimited text messages over TCP.

## Testing

The project includes a standalone Java integration test suite, `ChatServerTests.java`, which connects to a running server and verifies its behavior.

### Tests Performed

| Test | Description | Result |
|---|---|---|
| Multiple clients | Five clients connect and exchange messages | PASS |
| Message integrity | 100 numbered messages are received correctly | PASS |
| Concurrent broadcasting | Three clients send 50 messages each simultaneously | PASS |
| Disconnection handling | Remaining clients continue communicating after two disconnect | PASS |
| Repeated connections | Multiple connection and disconnection cycles | PASS |

### Test Results

```text
PASS  5 connected clients can broadcast
PASS  100 numbered messages arrive at every client
PASS  3 concurrent senders send 50 messages each
PASS  remaining clients work after two disconnect
PASS  repeated connection/disconnection cycles

RESULT: 5 passed, 0 failed
```

### Running the Tests

Start the chat server first.

Then compile and run the test suite:

```bash
javac ChatServerTests.java
java ChatServerTests
```

The tests connect to `127.0.0.1:5000`.

All five integration tests passed during local testing.

## Current Limitations

- **Thread-per-client architecture:** Each client requires a dedicated Java thread, which may limit scalability with very large numbers of connections.
- **Blocking I/O:** The server uses blocking socket operations instead of non-blocking I/O.
- **Synchronized broadcasting:** A shared lock is held during broadcasts, meaning a slow client can delay message delivery to other clients.
- **No persistent message history:** Messages are not stored and cannot be retrieved after disconnection.
- **No authentication:** Clients receive automatically generated identifiers rather than authenticated usernames.
- **No encryption:** Messages are transmitted as plaintext TCP traffic.
- **Basic error handling:** Failed socket writes are not comprehensively detected or recovered from.

The application is intended as a networking and multithreading learning exercise rather than a production-ready messaging service.
