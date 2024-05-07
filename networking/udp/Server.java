package networking.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {

    private static final int DATA_SIZE = 1024;

    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(8080); // listening socket
            byte[] receivedData = new byte[DATA_SIZE]; // byte array (buffer) which server receives

            while (true) {
                System.out.println("Server listening on port 8080...");

                DatagramPacket receivedPacket = new DatagramPacket(receivedData, DATA_SIZE);

                socket.receive(receivedPacket);

                System.out.println("Client on port: "+ receivedPacket.getPort());

                String message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

                if (message.equals("quit")) {
                    System.out.println("Server shutting down...");
                    break;
                } else {
                    System.out.println(message);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            socket.close();
        }
    }
}
