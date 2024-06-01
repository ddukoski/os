package networking.labs.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread {

    private int port;

    public TCPServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        ServerSocket serverSock = null;
        Socket clientSock = null;
        try {
            serverSock = new ServerSocket(port);
            System.out.println("Server started and listening on port " + port);
            while (true) {
                clientSock = serverSock.accept();
                System.out.println("Client connected with port " + clientSock.getPort());

                new Worker(clientSock).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                assert serverSock != null;
                assert clientSock != null;

                serverSock.close();
                clientSock.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TCPServer server = new TCPServer(7000);
        server.start();
    }
}
