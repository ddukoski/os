package networking.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private final int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.printf("Server starting on port %d...\n", this.port);

        ServerSocket serverSocket = null;
        try {

            /* The server does not need to connect with the user, so the user sends requests and the server
             ACCEPTS connections from outer sockets (line below), so we do not specify an IP address here */
            serverSocket = new ServerSocket(port);

            System.out.printf("Server started on port %d.\n", this.port);

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.printf("Client connected on port %d\n", socket.getPort());
                    new Worker(socket).start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }

    }

    public static void main(String[] args) {
        Server server = new Server(7000);
        server.start();
    }

}
