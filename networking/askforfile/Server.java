package networking.askforfile;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private final int serverPort;

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        open();
    }

    public void open() {
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {

            System.out.println("Server running on port " + serverSocket.getLocalPort());

            while (true) {
                Socket client = serverSocket.accept();
                new Worker(client).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        new Server(7000).start();
    }
}
