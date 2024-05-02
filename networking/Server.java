package networking;

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

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }

        System.out.printf("Server started on port %d.\n", this.port);

        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                throw new RuntimeException(e);
            }

            System.out.printf("Client connected on port %d\n", socket.getPort());
            new Worker(socket).start();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(7000);
        Client client = new Client(7000);
        server.start();
        client.start();
    }

}
