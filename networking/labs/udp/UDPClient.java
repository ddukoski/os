package networking.labs.udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient extends Thread{

    int serverPort;
    String hostname;

    public UDPClient(int serverPort, String hostname) {
        this.serverPort = serverPort;
        this.hostname = hostname;
    }

    @Override
    public void run(){

        DatagramSocket clientSock = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            clientSock = new DatagramSocket();

            while (true) {
                byte[] sendToServerBuf = br.readLine().getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendToServerBuf,
                        sendToServerBuf.length,
                        InetAddress.getByName(hostname),
                        serverPort);

                clientSock.send(sendPacket);

                System.out.println("Server response: ");

                byte[] receiveBuffer = new byte[2048];

                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                clientSock.receive(receivePacket);

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());

                System.out.println(response);

                if (response.equals("logged out")) {
                    break;
                }
            }
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
            throw new RuntimeException(exc);
        } finally {
            if (clientSock != null) clientSock.close();
            try {
                br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(3000);
        new UDPClient(8080, System.getenv("HOSTNAME")).start();
    }
}
