package networking.labs.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer extends Thread {

    private final int port;

    public UDPServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        DatagramSocket serverSock = null;

        try {
            serverSock = new DatagramSocket(port);

            System.out.println("UDP Server started on port: " + port);

            boolean loggedInAlready = false;

            while (true) {

                byte[] acceptBuf = new byte[2048];
                DatagramPacket acceptPacket = new DatagramPacket(acceptBuf, acceptBuf.length);

                serverSock.receive(acceptPacket);

                DatagramPacket response;

                String message = new String(acceptPacket.getData(), 0, acceptPacket.getLength());

                byte[] respondBuf;

                if (message.equals("login")) {

                    if (loggedInAlready) {
                        respondBuf = "already logged in".getBytes();
                    } else {
                        respondBuf = "logged in".getBytes();
                    }

                    loggedInAlready = true;

                } else if (message.equals("logout")) {
                    respondBuf = "logged out".getBytes();
                } else {
                    respondBuf = String.format("echo- %s", message).getBytes();
                }

                response = new DatagramPacket(
                        respondBuf,
                        respondBuf.length,
                        acceptPacket.getAddress(),
                        acceptPacket.getPort());

                serverSock.send(response);

                if (message.equals("logout")) {
                    break;
                }
            }
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
            throw new RuntimeException(exc);
        } finally {
            if (serverSock != null) serverSock.close();
        }

    }

    public static void main(String[] args) {
        new UDPServer(8080).start();
    }
}
