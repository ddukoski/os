package networking;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebRequest {
    public String command; //GET, POST, PUT, ...
    public String url;
    public String version; //HTTP version
    private Map<String, String> header;

    private WebRequest(String command, String url, String version, Map<String, String> header) {
        this.command = command;
        this.url = url;
        this.version = version;
        this.header = header;
    }

    public static WebRequest of(BufferedReader br){
        List<String> strings = new ArrayList<>();
        br.lines().forEach(strings::add);
        System.out.println(strings.get(0));

        String[] getLine = strings.get(0).split("\\s++");
        Map<String, String> headers = new HashMap<>();

        for(int i = 1 ; i < strings.size() - 1; ++i) {
            String[] parts = strings.get(i).split(":\\s++");
            headers.put( parts[0],parts[1] );
        }

        return new WebRequest(getLine[0], getLine[1], getLine[2], headers);
    }
}
