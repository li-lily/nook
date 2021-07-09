package main;

public class Pair {
    private int first;
    private int second;
    // ALERT: HARDCODED
    private int earCount;

    public Pair(int first, int second) {
        this.earCount = 3;

        this.first = first % (earCount + 1);
        this.second = second % (earCount + 1);

        if (this.first == 0) {
            this.first = earCount + 1;
        }
        if (this.second == 0) {
            this.second = earCount + 1;
        }

    }

    public Pair(int first, int second, int earCount) {
        this.earCount = earCount;
        this.first = first % (earCount + 1);
        this.second = second % (earCount + 1);

        if (this.first == 0) {
            this.first = earCount + 1;
        }
        if (this.second == 0) {
            this.second = earCount + 1;
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

        return (t.getSecond() == this.getSecond() && t.getSecond() == this.getSecond() && this.earCount == t.earCount);
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
