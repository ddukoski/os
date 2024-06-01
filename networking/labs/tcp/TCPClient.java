package networking.labs.tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient extends Thread {

    private int serverPort;
    private String hostname;

    public TCPClient(String hostname, int serverPort) {
        this.serverPort = serverPort;
        this.hostname = hostname;
    }

    @Override
    public void run() {
        Socket clientSock;
        BufferedReader reader = null;
        BufferedReader stdin = null;
        BufferedWriter writer = null;

        try {
            clientSock = new Socket(InetAddress.getByName(hostname), serverPort);

            reader = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(clientSock.getOutputStream()));
            stdin = new BufferedReader(new InputStreamReader(System.in));

            writer.write("login\n");
            writer.flush();

            while (true) {
                String serverResponse = reader.readLine();

                System.out.println(serverResponse);
                if (serverResponse.equals("logged out") || serverResponse.equals("connection closed")) break;

                String userInput = stdin.readLine() + "\n";

                writer.write(userInput);
                writer.flush();
            }


        } catch (IOException exc) {
            System.err.println(exc.getMessage());
            throw new RuntimeException(exc);
        } finally {
            try {
                assert reader != null;
                assert stdin != null;

                reader.close();
                writer.close();
                stdin.close();
            } catch (IOException exc) {
                System.err.println(exc.getMessage());
                throw new RuntimeException(exc);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(3000);
        String hostname = System.getenv("HOSTNAME");
        new TCPClient(hostname, 7000).start();
    }
}
