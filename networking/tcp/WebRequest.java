package networking.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebRequest {
    public String command; //GET, POST, PUT, ...
    public String url;
    public String version; //HTTP version
    public Map<String, String> header;

    private WebRequest(String command, String url, String version, Map<String, String> header) {
        this.command = command;
        this.url = url;
        this.version = version;
        this.header = header;
    }

    public static WebRequest of(BufferedReader br) throws IOException {
        List<String> strings = new ArrayList<>();
        String line;

        while (!(line = br.readLine()).isEmpty()) {
            strings.add(line);
        }

        String[] firstLine = strings.get(0).split("\\s++");
        Map<String, String> headers = new HashMap<>();

        for(int i = 1 ; i < strings.size(); ++i) {
            String[] parts = strings.get(i).split(":\\s++");
            headers.put( parts[0], parts[1] );
        }

        return new WebRequest(firstLine[0], firstLine[1], firstLine[2], headers);
    }
}
