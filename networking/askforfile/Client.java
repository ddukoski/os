package networking.askforfile;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {

    private final String hostname;
    private final int serverPort;

    public Client(String hostname, int serverPort) {
        this.hostname = hostname;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try {
            ask();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void ask() throws IOException {
        Socket client = new Socket(InetAddress.getByName(hostname), serverPort);

        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

        writer.write("phonebook.txt");
        writer.newLine();
        writer.flush();

        String line;

        while ((line =reader.readLine()) != null) {
            System.out.println(line);
        }

        reader.close();
        writer.close();
    }

    public static void main(String[] args) {
        String hostName = System.getenv("HOSTNAME");
        new Client(hostName, 7000).start();
    }
}
