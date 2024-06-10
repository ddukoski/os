package networking.writinginfile;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Worker extends Thread {

    private final Socket client;
    private static Semaphore accessFile = new Semaphore(1);

    public Worker(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            processRequest();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void processRequest() throws IOException, InterruptedException {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

        RandomAccessFile f = new RandomAccessFile("networking/writinginfile/" + reader.readLine(), "rw");
        f.seek(0);
        accessFile.acquire();
        String firstLn = f.readLine();
        f.seek(0);
        if (firstLn == null || firstLn.isBlank()) {
            f.write("1\n".getBytes());
        } else {
            int increment = Integer.parseInt(firstLn);
            f.write(String.format("%d\n", increment + 1).getBytes());
        }
        accessFile.release();

        f.seek(0);
        String line;
        while ((line = f.readLine()) != null){
            writer.write(line);
        }

        writer.newLine();
        writer.flush();
        reader.close();
        writer.close();
    }

}
