# Multithreaded TCP Chat Server

A multithreaded TCP chat server built with **Java**, allowing multiple clients to connect and exchange messages in real time.

The application uses Java's built-in socket API to establish TCP connections, assigning each connected client a dedicated thread. Messages are broadcast to all connected clients, while synchronization is used to coordinate access to shared resources.

This project was developed as a practical exercise in networking, multithreading, and concurrent programming.

## Features

- **Multiple client connections:** Supports multiple clients communicating simultaneously.
- **Multithreading:** Uses a dedicated thread to handle each connected client.
- **Message broadcasting:** Forwards incoming messages to all connected clients, including the sender.
- **Automatic client identification:** Assigns each client a unique numeric identifier, such as `Client 0` or `Client 1`.
- **Thread synchronization:** Uses synchronized blocks to prevent race conditions when accessing shared resources.
- **Disconnection handling:** Removes disconnected clients from the shared collection and closes their associated sockets.
- **Line-based communication:** Uses newline-delimited messages transmitted over TCP.

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
5. Each received message is broadcast to all connected clients using their `PrintWriter` instances.
6. When a client disconnects, its entry is removed from the shared collection and its socket is closed.

### Server Architecture

```text
                    TCP Server
                     Port 5000
                         |
                    ServerSocket
                         |
                     Main Thread
                         |
               Accepts Connections
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
                 Message Broadcasting
```

Each client handler independently receives incoming messages, while the shared client map allows messages to be forwarded to other connected clients.

### Thread Synchronization

Connected clients are stored in a shared `HashMap<String, PrintWriter>`.

Because multiple handler threads access this collection, a shared static lock is used to synchronize:

- Client registration
- Client ID generation
- Message broadcasting
- Client removal

Blocking socket reads are performed outside synchronized blocks, allowing handlers to wait for messages independently.

Broadcasting is synchronized to prevent concurrent modifications to the client collection and coordinate writes to connected clients.

## Requirements

- JDK 17 or later
- A terminal or TCP client such as Telnet

The project was developed and tested on Windows.

## How to Run

### 1. Start the Server

Clone the repository and navigate to the directory containing `Main.java`.

Compile the application:

```bash
javac Main.java
```

Run the server:

```bash
java Main
```

The server begins listening for TCP connections on port `5000`.

### 2. Connect Clients

Open a separate terminal for each client.

Using Telnet:

```bash
telnet localhost 5000
```

Repeat the command in additional terminals to establish multiple connections.

On Windows, the Telnet Client feature may need to be enabled before using the command.

### 3. Send Messages

Type a message into a connected client terminal and press Enter.

Messages are broadcast to every connected client and prefixed with the sender's assigned identifier.

Example:

```text
Client 0: Hello everyone!
Client 1: Hey!
Client 2: What's up?
```

Messages are transmitted as newline-delimited text over TCP.

## Performance and Reliability Testing

The server was evaluated locally using a standalone Java integration test program (`ChatServerTests.java`) and a PowerShell script for launching multiple client connections.

### Test Results

| Test Scenario | Workload | Result |
|---|---|---|
| Multiple connections | 5 simultaneous clients | PASS |
| Message integrity | 100 consecutive messages delivered to connected clients | PASS |
| Concurrent broadcasting | 3 clients sending 50 messages each simultaneously | PASS |
| Disconnection handling | 2 of 5 clients disconnecting | PASS |
| Connection cycling | 5 rounds of client connections and disconnections | PASS |
| Manual connection testing | Dozens of connections, reaching Client 49 | No observed failures |

**Integration
