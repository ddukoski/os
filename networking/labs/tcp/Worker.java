package networking.labs.tcp;

import java.io.*;
import java.net.Socket;

public class Worker extends Thread{

    private Socket clientSocket;
    public static int TOTAL_MESSAGES = 0;
    public Worker(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        boolean firstMessage = true;

        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            while (true) {

                String line;
                while ((line = reader.readLine()) == null) {}

                synchronized (Worker.class) {
                    TOTAL_MESSAGES++;
                }


                if (line.equals("login")) {
                    writer.write("logged in\n");
                } else if (line.equals("logout")) {
                    if (firstMessage){
                        writer.write("connection closed\n");
                        break;
                    }
                    writer.write("logged out\n");
                    break;
                } else {
                    if (firstMessage) {
                        writer.write("connection closed\n");
                        break;
                    }
                    writer.write("echo: " + line + "\n");
                }
                writer.flush();
                firstMessage = false;
            }


        } catch (IOException exc) {
            System.err.println(exc.getMessage());
            throw new RuntimeException(exc);
        } finally {
            try {
                assert reader != null;
                assert writer != null;

                System.out.println(TOTAL_MESSAGES);
                writer.flush();
                reader.close();
                writer.close();
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            }
        }
    }

}
