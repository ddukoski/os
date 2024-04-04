package sync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

public class ElementCount {

    static long count = 0;
    static long max = Integer.MIN_VALUE;

    static final BoundedRandomGenerator random = new BoundedRandomGenerator();
    private static final int ARRAY_LENGTH = 10000;
    private static final int NUM_THREADS = 10;

    private static int finished = NUM_THREADS;

    private static final int SEARCH_TARGET = 5;  // This is the target element we are looking for

    // TODO: Define sychronization elements and initialize

    // DO NOT CHANGE
    public static int[] getSubArray(int[] array, int start, int end) {
        return Arrays.copyOfRange(array, start, end);
    }

    public static void main(String[] args) throws InterruptedException {

        int[] arr = ArrayGenerator.generate(ARRAY_LENGTH, SEARCH_TARGET);

        // TODO: Make the SearchThread class a thread and start 10 instances
        // Each instance should take a subarray from the original array with equal length

        List<CountThread> countThreads = new ArrayList<>();
        int elementsPerThread = arr.length / NUM_THREADS;

        for (int i = 0; i < NUM_THREADS; i++) {
            int[] subarray = getSubArray(arr, i * elementsPerThread, (i + 1) * elementsPerThread);
            countThreads.add(new CountThread(subarray, SEARCH_TARGET));
        }

        for (CountThread countThread : countThreads) {
            countThread.start();
        }

        for (CountThread countThread : countThreads) {
            countThread.join();
        }
        // DO NOT CHANGE

        System.out.println("The number of total counted elements is: " + count);
        System.out.println("The generated number of elements is: " + ArrayGenerator.elementCount);

        // TODO: The max thread should print the number of occurences

    }

    // TO DO: Make the SearchThread class a thread
// You can add methods or attributes if necessary
    static class CountThread extends Thread {

        private final static Lock arrayMutex = new ReentrantLock();
        private final static Semaphore revealMax = new Semaphore(0);


        private final int[] arr;
        private final int target;
        private int localCount = 0;

        public CountThread(int[] arr, int target) {
            this.arr = arr;
            this.target = target;
        }

        public void countElements() {
            arrayMutex.lock();
            for (int num : this.arr) {
                if (num == target) {
                    count++;
                    localCount++;
                }
            }
            arrayMutex.unlock();

            System.out.println("Local count of " + Thread.currentThread().threadId() + " is: " + localCount);
        }

        public void countElementsParallel() throws InterruptedException {
            countElements();

            arrayMutex.lock();

            if (localCount > max) {
                max = localCount;
            }
            finished--;
            if (finished == 0) {
                revealMax.release(NUM_THREADS);
            }

            arrayMutex.unlock();


            revealMax.acquire(); //stuck here
            arrayMutex.lock();
            if (localCount == max)
                System.out.printf("\nThread %d says: %d, so I'm the highest!%n",
                        Thread.currentThread().threadId(), localCount);
            arrayMutex.unlock();
        }

        public void run() {
            try {
                countElementsParallel();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    //************ DO NOT CHANGE ************//
    static class BoundedRandomGenerator {
        static final Random random = new Random();
        static final int RANDOM_BOUND = 100;

        public int nextInt() {
            return random.nextInt(RANDOM_BOUND);
        }

    }

    static class ArrayGenerator {

        static int elementCount;

        static int[] generate(int length, int target) {
            int[] array = new int[length];

            for (int i = 0; i < length; i++) {
                int element = ElementCount.random.nextInt();

                if (element == target) {
                    elementCount++;
                }

                array[i] = element;
            }

            return array;
        }
    }


}

