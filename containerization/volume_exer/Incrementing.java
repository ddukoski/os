package containerization.volume_exer;

import java.io.*;

public class Incrementing {
    public static void main(String[] args) throws IOException {
        BufferedReader br =  new BufferedReader( new FileReader("increment.txt") );
        int num = Integer.parseInt(br.readLine());

        System.out.println(num);

        FileWriter fw = new FileWriter("increment.txt");
        fw.write(Integer.toString(num + 1));
        fw.close();
    }
}
