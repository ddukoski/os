package networking.writinginfile;

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
        startServer();
    }

    public void startServer() {
        try (ServerSocket socket = new ServerSocket(port)) {

            System.out.println("Server started on port " + port);
            while (true) {
                Socket toProcess = socket.accept();
                new Worker(toProcess).start();
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Server(7000).start();
    }
}
