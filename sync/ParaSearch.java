package sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParaSearch {

    public static void main(String[] args) throws InterruptedException {
        List<Integer> searchInside = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            Random r = new Random();
            int insert = r.nextInt(10000);
            searchInside.add(insert);
        }

        int divided = searchInside.size() / 4;

        List<Thread> tl = new ArrayList<>();

        tl.add(new Searcher(searchInside, 0, divided, 300));
        tl.add(new Searcher(searchInside, 2 * divided, 3 * divided, 300));
        tl.add(new Searcher(searchInside, divided, 2 * divided, 300));
        tl.add(new Searcher(searchInside, 3 * divided, 1500, 300));

        tl.forEach(Thread::start);

        for (Thread thread : tl) {
            thread.join();
        }

        if (Searcher.result == null) {
            System.err.println("No number in the list");
        } else {
            System.out.println("Value is at index: " + Searcher.result + "\nProof: " +
                    searchInside.get(Searcher.result));
        }
    }
}

class Searcher extends Thread {
    private final List<Integer> integerList;
    private final int from;
    private final int to;
    private final int what;
    public static Integer result;

    public Searcher(List<Integer> integerList, int from, int to, int what) {
        this.integerList = integerList;
        this.from = from;
        this.to = to;
        this.what = what;
        Searcher.result = null;
    }

    public synchronized void search() {
        for (int i = from; i < to; i++) {
            if (integerList.get(i) == what) {
                Searcher.result = i;
                return;
            }
        }
    }

    @Override
    public void run() {
        search();
    }
}
