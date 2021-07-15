package main;

public class Pair {
    private int first;
    private int second;
    // ALERT: HARDCODED
    private int earCount;

    public Pair(int first, int second) {
        this.earCount = Rabbit.defaultEarCount;

        first = first % (earCount + 1);
        second = second % (earCount + 1);


        if (first == 0) {
            first = earCount + 1;
        }
        if (second == 0) {
            second = earCount + 1;
        }

        if (first < second) {
            this.first = first;
            this.second = second;
        } else {
            this.first = second;
            this.second = first;
        }

    }

    public Pair(int first, int second, int earCount) {
        this.earCount = earCount;
        first = first % (earCount + 1);
        second = second % (earCount + 1);

        if (first == 0) {
            first = earCount + 1;
        }
        if (second == 0) {
            second = earCount + 1;
        }

        if (first < second) {
            this.first = first;
            this.second = second;
        } else {
            this.first = second;
            this.second = first;
        }

    }

    public boolean equals(Pair t) {

        // If the object is compared with itself then return true
        if (t == this) {
            return true;
        }

        if (t == null) {
            return false;
        }

        return (t.getFirst() == this.getFirst() && t.getSecond() == this.getSecond() && this.earCount == t.earCount);
    }

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }

    public int getEarCount() {
        return earCount;
    }

}
