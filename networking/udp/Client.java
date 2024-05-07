package networking.udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Client extends Thread {

    private InetAddress IPAddress;

    public Client(InetAddress IPAddress) throws SocketException {
        this.IPAddress = IPAddress;
    }

    public void send() throws IOException {
        // used to read input from stdin stream, NOT reading from server
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        DatagramSocket clientSocket = new DatagramSocket();


        System.out.println("Enter message to send to server ('quit' for shutdown):");
        String message;
        while (!(message = br.readLine()).isEmpty() && !message.equals("quit")) {
            byte[] sendDataToServer = message.getBytes();

            DatagramPacket packetSend = new DatagramPacket(sendDataToServer, sendDataToServer.length, IPAddress, 8080);
            clientSocket.send(packetSend);
        }

        byte[] sendDataToServer = "quit".getBytes();
        DatagramPacket packetSend = new DatagramPacket(sendDataToServer, sendDataToServer.length, IPAddress, 8080);

        clientSocket.send(packetSend);

        byte[] receive = new byte[1024];
        DatagramPacket finish = new DatagramPacket(receive,1024);
        clientSocket.receive(finish);
        System.out.printf("Server says: %s", new String(finish.getData(), 0, finish.getLength()));

        clientSocket.close();
        br.close();
    }

    @Override
    public void run() {
        try {
            send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws UnknownHostException, SocketException {
        Client client = new Client(InetAddress.getByName("localhost"));
        client.start();
    }
}