package containerization.volume_exer;

import java.io.*;
import java.util.Scanner;

public class Incrementing {
    public static void main(String[] args) throws IOException {
        File make = new File(".\\datavirt\\increment.txt");
        int num;
        if (make.createNewFile()) {
            num = 0;
        } else {
            Scanner s = new Scanner(make);
            num = Integer.parseInt(s.nextLine()) + 1;
        }

        FileWriter fw = new FileWriter(".\\datavirt\\increment.txt");
        fw.write(Integer.toString(num));
        System.out.println(num);
        fw.close();
    }
}
