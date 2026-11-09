import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException{
        ServerSocket mainSocket = new ServerSocket(5000);

        while (true){
            Socket socket = mainSocket.accept();

            Thread thread = new Thread(() -> new ClientHandler(socket).run());
            thread.start();

        }
   }

   public static class ClientHandler implements Runnable {
        Socket socket;
        private static Map<String, PrintWriter> connectedClients = new HashMap<>();
        private final static Object lock = new Object();
        public static int clientCount = 0;

        public ClientHandler(Socket socket){
            this.socket = socket;
        }

       @Override
       public void run() {
                String name = "";
                System.out.println("Handling client: " + this.socket.getInetAddress());
                System.out.println("Handling client on thread: " + Thread.currentThread().getName());
                try (Socket client = socket){

                    // Handle input
                    InputStream is = socket.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader in = new BufferedReader(isr);

                    // Handle output
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    synchronized (lock) {
                        name = "Client " + clientCount;
                        connectedClients.put(name, out);
                        clientCount++;

                    }

                        String line = "";
                        line = in.readLine();

                        while (line != null) {
                                    synchronized (lock){
                                        for (Map.Entry<String, PrintWriter> entry : connectedClients.entrySet()) {
                                            //                                    if (entry.getValue() == out) continue;
                                            entry.getValue().println(name + ": " + line);
                                        }
                                    }
                                line = in.readLine();

                        }

                } catch (IOException e){
                    System.out.println(e.getMessage());
                } finally {
                    synchronized (lock) {
                        connectedClients.remove(name);
                    }
                }

            }
       }


}
