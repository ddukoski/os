package sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class SyncBuffer {

    public static int numControllers;
    public static Semaphore numAccessing;
    public static Semaphore executionMutex;
    public static Semaphore cSem;

    public static void init() {
        SyncBuffer.numControllers = 0;
        SyncBuffer.executionMutex = new Semaphore(1); //mutex
        SyncBuffer.numAccessing = new Semaphore(1); //mutex
        SyncBuffer.cSem = new Semaphore(10);
    }

    public static void main(String[] args) throws InterruptedException {
        init();
        List<Integer> al = new ArrayList<>();
        al.add(10);
        al.add(20);
        al.add(30);
        al.add(40);

        Buffer b = new Buffer(new ArrayList<>(al));

        List<Thread> threadList = new ArrayList<>();

        IntStream.range(0, 20).forEach(i -> threadList.add(new Producer(b)));
        IntStream.range(0, 35).forEach(i -> threadList.add(new Controller(b)));

        threadList.forEach(Thread::start);

        for (Thread thread : threadList) {
            thread.join();
        }

        System.out.println(b.removed);
        System.out.println(b.removed.size());
    }
}

class Producer extends Thread {

    private Buffer buffer;

    public Producer(Buffer b) {
        this.buffer = b;
    }

    public void execute() throws InterruptedException {
        SyncBuffer.executionMutex.acquire();
        buffer.produce();
        SyncBuffer.executionMutex.release();
    }

    @Override
    public void run() {
        try {
            this.execute();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class Controller extends Thread {

    private final Buffer buffer;

    // unnecessary if the buffer check/removal is always on a different number ( it's not in our case )
    private static final Lock restrictSameItemCheck = new ReentrantLock();

    public Controller(Buffer b) {
        this.buffer = b;
    }

    public void execute() throws InterruptedException {
        SyncBuffer.numAccessing.acquire();
        if (SyncBuffer.numControllers == 0) {
            SyncBuffer.executionMutex.acquire();
        }
        SyncBuffer.numControllers++;
        SyncBuffer.numAccessing.release();


        // Item check & removal (in buffer)
        SyncBuffer.cSem.acquire();

        restrictSameItemCheck.lock();
        buffer.check();
        restrictSameItemCheck.unlock();
        SyncBuffer.cSem.release();

        SyncBuffer.numAccessing.acquire();

        // this controller (thread) is finished with checking, it will never access buffer again
        SyncBuffer.numControllers--; // this can be between line 100 and 99 with no problem (but lock first)
        if (SyncBuffer.numControllers == 0) {

            // last controller stopping, allow producer to enter if it must
            SyncBuffer.executionMutex.release();
        }
        SyncBuffer.numAccessing.release();
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class Buffer {
    private List<Integer> buffer;
    public List<Integer> removed;

    public Buffer(List<Integer> initialBuf) {
        this.buffer = new ArrayList<>(initialBuf);
        this.removed = new ArrayList<>();
    }

    public void produce() {
        Random r = new Random();
        buffer.add(r.nextInt(100));
    }

    public void check() {
        if (!buffer.isEmpty()) {
            removed.add(buffer.remove(0));
        }
    }
}
