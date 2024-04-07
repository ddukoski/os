package sync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* Оваа задача е повеќе за вежбање на Multithreading наместо синхронизација, иаку може редоследен проблем да настане
   при користење на нитките БЕЗ взаемно исклучување */

public class TraverseMatrix {

    public static int NUM_THREADS = 15;

    public static void main(String[] args) throws InterruptedException {

        DataMatrix dataMatrix = new DataMatrix(); // automatically create a randomized String matrix with numbers and characters
        System.out.println("\nOriginal generated matrix: ");
        System.out.println(dataMatrix);

        StatisticsResource statisticsResource = new StatisticsResource(); // shared accumulator reference

        List<Concatenator> concatenators = new ArrayList<>();

        for (int i = 0; i < NUM_THREADS; i++) {
            concatenators.add(new Concatenator(statisticsResource, dataMatrix, i));
        }

        concatenators.forEach(Concatenator::run);

        for (int i = 0; i < NUM_THREADS; i++) {
            concatenators.get(i).join();
        }

        statisticsResource.printString();
        System.out.printf("Original: %d\n", dataMatrix.countStrings);
    }

    static class DataMatrix {
        private final String[][] data;
        public int countStrings;

        public DataMatrix() {
            this.countStrings = 0;
            this.data = this.generateMatrix();
        }

        public String[][] generateMatrix() {

            Random r = new Random();

            int lenRows = r.nextInt(5, 10);

            String[][] matrix = new String[NUM_THREADS][lenRows];


            for (int i = 0; i < NUM_THREADS; i++) {
                for (int j = 0; j < lenRows; j++) {
                    double rNum = Math.random();

                    if (rNum < 0.7) {
                        matrix[i][j] = Integer.toString(r.nextInt(10));
                    } else {
                        double rNumStr = Math.random();
                        this.countStrings++;
                        if (rNumStr < 0.5)
                            matrix[i][j] = "s";
                        else
                            matrix[i][j] = "t";
                    }
                }
            }

            return matrix;
        }

        public String getEl(int i, int j) {
            return data[i][j];
        }

        public int getN() {
            return data.length;
        }

        public int getM() {
            return data[0].length;
        }

        public boolean isString(int i, int j) {
            return Character.isAlphabetic(data[i][j].charAt(0));
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (String[] strArr : data) {
                sb.append(Arrays.toString(strArr)).append("\n");
            }
            return sb.toString();
        }

    }

    static class StatisticsResource {
        private String catString;

        public StatisticsResource() {
            catString = "";
        }

        public void concatenateString(String charCat) {
            catString += charCat;
        }

        public void printString() {
            System.out.println(catString);
            System.out.printf("Length of the string ( USING THREADS ): %d\n", catString.length());
        }
    }

    static class Concatenator extends Thread {
        private final StatisticsResource statisticsResource;
        private final DataMatrix dataMatrix;
        private final int myRow;
        private static final Lock mutex = new ReentrantLock();

        public Concatenator(StatisticsResource statisticsResource, DataMatrix dataMatrix, int row) {
            this.statisticsResource = statisticsResource;
            this.dataMatrix = dataMatrix;
            this.myRow = row;
        }

        public void concatenate_by_row(int row) {
            for (int i = 0; i < dataMatrix.getM(); i++) {
                if (dataMatrix.isString(row, i)) {
                    mutex.lock();
                    statisticsResource.concatenateString(dataMatrix.getEl(row, i));
                    mutex.unlock();
                }
            }
        }

        @Override
        public void run() {
            concatenate_by_row(myRow);
        }
    }
}
