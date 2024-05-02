package networking;

import java.io.*;
import java.net.Socket;

public class Worker extends Thread{

    private Socket socket;

    public Worker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream socketInput;
        OutputStream socketOutput;
        try {
             socketInput = socket.getInputStream();
             socketOutput = socket.getOutputStream();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(socketInput));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketOutput));

        WebRequest wr = WebRequest.of(reader);
        System.out.println(wr.command + " " + wr.url);

        try {
            writer.write("HTTP/1.1 200 OK\n");
            writer.write("Content-Type: text/html\n\n"); //The other headers are meta-data of the browser and are not written
            writer.write("Hello World!\n");
            writer.flush();
            reader.close();
            writer.close();
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
