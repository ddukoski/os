package containerization.labs.decibel.monitor;

import java.io.*;
import java.util.Arrays;

public class SoundLevelMonitor {
    public static void main(String[] args) throws IOException, InterruptedException {

        Thread.sleep(2000);

        BufferedReader br = new BufferedReader
                (new InputStreamReader(new FileInputStream("./shared/soundlevel.txt")));

        File pollution = new File("./shared/noisepollution.txt");
        pollution.createNewFile();
        FileWriter fw = new FileWriter(pollution);
        BufferedWriter writer = new BufferedWriter(fw);

        String[] low = System.getenv("LOW_SOUNDLEVEL").split(",");
        String[] med = System.getenv("MEDIUM_SOUNDLEVEL").split(",");
        String high = System.getenv("HIGH_SOUNDLEVEL");

        System.out.println(Arrays.toString(low));
        System.out.println(Arrays.toString(med));
        System.out.println(high);

        int medium = Integer.parseInt(med[0]);
        int higher = Integer.parseInt(high);

        while (true) {

            int sum = 0;

            for (int i = 0; i < 10; i++) {
                String lineRead = br.readLine();
                if (lineRead == null || lineRead.isEmpty()) {
                    --i;
                    continue;
                }
                int level = Integer.parseInt(lineRead);
                sum += level;
            }


            double average = (double) sum / 10;

            String pollutionLevel;

            if (average > higher) {
                pollutionLevel = "HIGH";
            } else if (average >= medium) {
                pollutionLevel = "MEDIUM";
            } else {
                pollutionLevel = "LOW";
            }

            writer.write(pollutionLevel + "\n");
            writer.flush();

            System.out.println(pollutionLevel);
            Thread.sleep(30000);

        }
    }
}
