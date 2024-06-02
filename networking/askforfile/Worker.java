package networking.askforfile;

import java.io.*;
import java.net.Socket;

public class Worker extends Thread{
    private final Socket client;

    public Worker(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            processRequest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processRequest() throws IOException {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            sendFileContents(reader.readLine(), writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            assert writer != null;
            writer.close();
            reader.close();
            client.close();
        }
    }

    public void sendFileContents(String ofFile, BufferedWriter writer) throws IOException {
        File f = new File("./shared/" + ofFile);

        if (!f.exists()) {
            writer.write("File does not exist.");
            writer.newLine();
            writer.flush();
            return;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        String line;
        while ((line = br.readLine()) != null) {
            writer.write(line);
            writer.newLine();
        }

        writer.flush();
    }
}
