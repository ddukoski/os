package containerization.dockercompose.backend.src;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;

public class SimpleHttpServer {
    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new java.net.InetSocketAddress(8080), 0);

        server.createContext("/", new MyHandler());

        server.setExecutor(null); // creates a default executor

        server.start();

        System.out.println("Server is running on port 8080...");

    }


    static class MyHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange t) throws IOException {

            String response = "Hello from Java HTTP server!";

            t.sendResponseHeaders(200, response.length());

            OutputStream os = t.getResponseBody();

            os.write(response.getBytes());

            os.close();

        }

    }
}
