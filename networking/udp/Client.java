package networking.udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        InetAddress IPAddress = InetAddress.getByName("localhost");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter message to send to server ('quit' for shutdown):");
        String message;
        while (!(message = br.readLine()).isEmpty() && !message.equals("quit")) {
            byte[] sendDataToServer = message.getBytes();

            DatagramPacket packetSend = new DatagramPacket(sendDataToServer, sendDataToServer.length, IPAddress, 8080);
            socket.send(packetSend);
        }

        byte[] sendDataToServer = "quit".getBytes();
        DatagramPacket packetSend = new DatagramPacket(sendDataToServer, sendDataToServer.length, IPAddress, 8080);
        socket.send(packetSend);
        
        socket.close();
        br.close();
    }
}