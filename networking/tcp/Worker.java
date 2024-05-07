package networking.tcp;

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

             BufferedReader reader = new BufferedReader(new InputStreamReader(socketInput));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketOutput));
             WebRequest req = WebRequest.of(reader);

             System.out.printf("%s %s %s\n",req.command, req.url, req.version);

             writer.write("HTTP/1.1 200 OK\n");

             //The other headers are meta-data of the browser and are not written -> \n\n specifies end of HTML response
             writer.write("Content-Type: text/html\n\n");
             writer.write(String.format("Hello there, %s\n", req.header.get("User-Agent")));

             //flush() will IMMEDIATELY write anything left in the stream and clear it of any elements inside it
             writer.flush();

             reader.close();
             writer.close();
             this.socket.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
