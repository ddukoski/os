package networking.writinginfile;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread {

    private int serverPort;

    public Client(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        ask();
    }

    public void ask() {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try (Socket socket = new Socket("localhost", serverPort)) {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String line;

            writer.write("counter.txt");
            writer.newLine();
            writer.flush();

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            writer.close();
            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Client(7000).start();
    }
}
