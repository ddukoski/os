package containerization.labs.temperature.monitor;

import java.io.*;

public class TemperatureMonitor {
    public static void main(String[] args) throws IOException, InterruptedException {

        Thread.sleep(1000);

        BufferedReader br = new BufferedReader
                (new InputStreamReader(new FileInputStream("shared/temperature.txt")));

        File tempLevel = new File("shared/temperaturelevel.txt");
        tempLevel.createNewFile();
        FileWriter fw = new FileWriter(tempLevel);
        BufferedWriter writer = new BufferedWriter(fw);

        int highLimit = Integer.parseInt(System.getenv("HIGH"));
        int mediumLimit = Integer.parseInt(System.getenv("MEDIUM").split(",")[0]);

        while (true) {

            int sum = 0;

            for (int i = 0; i < 5; i++) {
                String lineRead = br.readLine();
                if (lineRead == null || lineRead.isEmpty()) {
                    i--;
                    continue;
                }
                int level = Integer.parseInt(lineRead);
                sum += level;
            }

            double average = (double) sum / 5;

            String levelShow;

            if (average > highLimit) {
                levelShow = "HIGH";
            } else if (average >= mediumLimit) {
                levelShow = "MEDIUM";
            } else {
                levelShow = "LOW";
            }

            writer.write(levelShow + "\n");
            writer.flush();

            System.out.println(levelShow);
            Thread.sleep(60000);

        }
    }
}
