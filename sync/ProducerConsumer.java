package sync;

import java.util.ArrayList;
import java.util.Arrays;
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

        private final Buffer buffer;
        private final int consumerId;

        public Consumer(int consumerId, Buffer buffer) {
            this.buffer = buffer;
            this.consumerId = consumerId;
        }

        public void execute() throws InterruptedException {

            //stop ANY thread (reason for array usage) for continuing if the buffer is not full
            productPermissions[consumerId].acquire();
            buffer.getItem(consumerId);

            // One and ONLY 1 item must execute due to the race condition of the number of items in the buffer
            execMutex.acquire();
            buffer.decrementNumberOfItemsLeft();
            // Check if the buffer is empty, if it is, then this is the last Consumer emptying
            if (buffer.isBufferEmpty()) {
            // Pass over control to producer, this is the last one and no other Consumer is waiting to take the mutex
                emptyBuffer.release();
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
        private final Buffer buffer;

        public Producer(Buffer buffer) {
            this.buffer = buffer;
        }

        public void execute() throws InterruptedException {
            emptyBuffer.acquire(); // Take control of the buffer and fill it up

            execMutex.acquire();
            buffer.reload(); // Fill the buffer

            // Can be after critical section too, but this will put Consumers ahead for a bit and then wait for a mutex
            Arrays.stream(productPermissions).forEach(Semaphore::release);
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

    static class Buffer {

        // How many items are in the buffer currently
        private int numItems = 0;

        // Total capacity of the buffer
        private final int numConsumers;

        public Buffer(int numConsumers) {
            this.numConsumers = numConsumers;
        }

        public void reload() {
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
            System.out.printf("Get item for consumer with id: %d.%n", consumerId);
        }
    }


}

