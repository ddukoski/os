package containerization.labs.decibel.sensor;

import java.io.*;
import java.util.Random;

public class SoundLevelSensor {
    public static void main(String[] args) throws IOException, InterruptedException {

        File soundLevel = new File("./shared/soundlevel.txt");
        soundLevel.createNewFile();
        FileWriter fw = new FileWriter(soundLevel);
        BufferedWriter writer = new BufferedWriter(fw);

        while (true) {

            Random r = new Random();
            ;
            for (int i = 0; i < 10; i++) {
                int write = r.nextInt(40, 101);
                System.out.println(write);
                writer.write(String.format("%d\n", write));
                writer.flush();
            }


            Thread.sleep(20000);// 20 sekundi se mn za cekanje :P
        }
    }
}
