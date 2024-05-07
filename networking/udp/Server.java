package networking.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server extends Thread{

    private static final int DATA_SIZE = 1024;
    private int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        DatagramSocket serverSocket = null;
        try {
            byte[] buffer = new byte[DATA_SIZE]; // byte array (buffer) which server receives

            serverSocket = new DatagramSocket(port);

            while (true) {
                System.out.println("Server listening on port 8080...");

                DatagramPacket receivedPacket = new DatagramPacket(buffer, DATA_SIZE);

                serverSocket.receive(receivedPacket);

                System.out.printf("Client data port: %d\n", receivedPacket.getPort());

                // since we are reading from a byte array of 1024 bytes available (1024 chars), we need to limit
                // the output buffer to a number of bytes exactly equal to the length of the message,
                // to avoid bad output, by using the String constructor below
                String message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

                if (message.equals("quit")) {
                    System.out.println("Server shutting down...");
                    byte[] finish = "We're done!".getBytes();
                    DatagramPacket confirmation = new DatagramPacket(finish, finish.length, InetAddress.getByName("localhost"), receivedPacket.getPort());
                    serverSocket.send(confirmation);

                    break;
                } else {
                    System.out.println(message);
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            assert serverSocket != null;
            serverSocket.close();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8080);
        server.start();
    }
}
