package sync;

import java.util.concurrent.Semaphore;
import java.util.function.IntConsumer;

// LeetCode problem 1116

public class PrintZeroEvenOdd {
    public static void main(String[] args) {

        ZeroEvenOdd shared = new ZeroEvenOdd(5);

        Thread callsOdd  = new CallerOdd(shared);
        Thread callsEven = new CallerEven(shared);
        Thread callsZero = new CallerZero(shared);

        callsOdd    .start();
        callsEven   .start();
        callsZero   .start();
    }
}

class CallerOdd extends Thread {

    private ZeroEvenOdd shared;

    public CallerOdd(ZeroEvenOdd shared) {
        this.shared = shared;
    }

    @Override
    public void run() {
        try {
            shared.odd(System.out::println);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class CallerEven extends Thread {

    private ZeroEvenOdd shared;

    public CallerEven(ZeroEvenOdd shared) {
        this.shared = shared;
    }

    @Override
    public void run() {
        try {
            shared.even(System.out::println);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class CallerZero extends Thread {

    private ZeroEvenOdd shared;

    public CallerZero(ZeroEvenOdd shared) {
        this.shared = shared;
    }

    @Override
    public void run() {
        try {
            shared.zero(System.out::println);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class ZeroEvenOdd {
    private int n;
    private int counter;
    private int limitOdd;
    private int limitEven;
    private Semaphore odd;
    private Semaphore even;
    private Semaphore zero;

    public ZeroEvenOdd(int n) {
        this.n = n;
        this.counter = 1;
        this.limitEven = n / 2;
        if (n % 2 == 0) {
            limitOdd = limitEven;
        } else {
            limitOdd = limitEven + 1;
        }
        this.odd = new Semaphore(0);
        this.zero = new Semaphore(1);
        this.even = new Semaphore(0);
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            zero.acquire();
            printNumber.accept(0);
            if (counter % 2 == 0) {
                even.release();
            } else {
                odd.release();
            }
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        for (int i = 0; i < limitEven; i++) {
            even.acquire();
            printNumber.accept(counter++);
            zero.release();
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        for (int i = 0; i < limitOdd; i++) {
            odd.acquire();
            printNumber.accept(counter++);
            zero.release();
        }
    }
}