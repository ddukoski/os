package containerization.labs.temperature.sensor;

import java.io.*;
import java.util.Random;

public class TemperatureSensor {
    public static void main(String[] args) throws IOException, InterruptedException {

        File temperature = new File("shared/temperature.txt");
        temperature.createNewFile();
        FileWriter fw = new FileWriter(temperature);
        BufferedWriter writer = new BufferedWriter(fw);

        while (true) {

            Random r = new Random();

            for (int i = 0; i < 5; i++) {
                int write = r.nextInt(5, 51);
                writer.write(String.format("%d\n", write));
                writer.flush();
            }

            Thread.sleep(30000);
        }
    }
}
