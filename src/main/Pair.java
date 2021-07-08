package main;

public class Pair {
    private int first;
    private int second;
    // ALERT: HARDCODED
    private int earCount = Rabbit.earCount;

    public Pair(int first, int second) {
        this.first = first % (earCount + 1);
        this.second = second % (earCount + 1);

        if (this.first == 0) {
            this.first = earCount + 1;
        }
        if (this.second == 0) {
            this.second = earCount + 1;
        }

    }

    public int getFirst() {
        return first;
    }


    public int getSecond() {
        return second;
    }
}
