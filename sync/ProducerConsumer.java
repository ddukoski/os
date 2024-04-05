package sync;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ProducerConsumer {
    static final int NUM_RUNS = 100;
    static final int NUM_CONSUMERS = 50;

    // Synchronization elements
    public static Semaphore execMutex;
    public static Semaphore[] productPermissions;
    public static Semaphore emptyBuffer;

    // Initialization of synchronization elements
    public static void init() {
        execMutex = new Semaphore(1);
        productPermissions = new Semaphore[NUM_CONSUMERS];
        for (int i = 0; i < NUM_CONSUMERS; ++i) {
            productPermissions[i] = new Semaphore(0);
        }
        emptyBuffer = new Semaphore(1);
    }

    public static void main(String[] args) {
        init();

        Buffer sharedBuffer = new Buffer(NUM_CONSUMERS);
        Producer p = new Producer(sharedBuffer);
        p.start();

        List<Consumer> consumers = new ArrayList<>();
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            consumers.add(new Consumer(i, sharedBuffer));
        }

        for (int i = 0; i < NUM_CONSUMERS; i++) {
            consumers.get(i).start();
        }
    }

    static class Consumer extends Thread {

        private Buffer buffer;
        private int consumerId;

        public Consumer(int consumerId, Buffer buffer) {
            this.buffer = buffer;
            this.consumerId = consumerId;
        }

        public void execute() throws InterruptedException {
            productPermissions[consumerId].acquire();
            buffer.getItem(consumerId);

            execMutex.acquire(); // ONLY 1 THREAD IS EXECUTING THIS
            buffer.decrementNumberOfItemsLeft();
            if (buffer.isBufferEmpty()) { // the last thread is executing
                emptyBuffer.release(); // buffer needs to be filled, so pass control to producer
            }
            execMutex.release();
        }

        @Override
        public void run() {
            for (int i = 0; i < ProducerConsumer.NUM_RUNS; i++) {
                try {
                    execute();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static class Producer extends Thread {
        private Buffer buffer;

        public Producer(Buffer buffer) {
            this.buffer = buffer;
        }

        public void execute() throws InterruptedException {
            emptyBuffer.acquire();

            execMutex.acquire();
            buffer.fillBuffer();
            execMutex.release();

            for (int i = 0; i < NUM_CONSUMERS; i++) {
                productPermissions[i].release(1);
            }
        }

        @Override
        public void run() {
            for (int i = 0; i < ProducerConsumer.NUM_RUNS; i++) {
                try {
                    execute();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static class Buffer {

        // How many items are in the buffer currently
        private int numItems = 0;

        // Total capacity of the buffer
        private final int numConsumers;

        public Buffer(int numConsumers) {
            this.numConsumers = numConsumers;
        }

        public int getBufferCapacity() {
            return numConsumers;
        }

        public void fillBuffer() {
            if (numItems != 0) {
                throw new RuntimeException("The buffer is not empty!");
            }
            numItems = numConsumers;
            System.out.println("The buffer is full.");
        }

        public void decrementNumberOfItemsLeft() {
            if (numItems <= 0) {
                throw new RuntimeException("Can't get item, no items left in the buffer!");
            }
            numItems--;
        }

        public boolean isBufferEmpty() {
            return numItems == 0;
        }

        public void getItem(int consumerId) {
            System.out.println(String.format("Get item for consumer with id: %d.", consumerId));
        }
    }


}

