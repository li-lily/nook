package main;


import java.io.InvalidObjectException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class DehnTwist extends Rabbit {
    private Pair identifier;
    private int exp;

    /** Constructor takes in two points and an exponent **/
    public DehnTwist(int first, int second, int exp) {

        this.exp = exp;

        if (first == second) {
            throw new InvalidParameterException("The Dehn Twist input does not surround two distinct points.");
        } else if (first < second) {
            this.identifier = new Pair(first, second);
        } else {
            this.identifier = new Pair(second, first);
        }
    }

    /** Extra constructor takes in identifier and an exponent **/
    public DehnTwist(Pair identifier, int exp) {

        this.exp = exp;
        this.identifier = identifier;
    }

    public Pair getidentifier() {
        return identifier;
    }

    public int getExp() {
        return exp;
    }

    public boolean is_liftable() {
        return !((identifier.getSecond() == Rabbit.earCount + 1) && (exp % 2 == 1));
    }

    /** Decomposes non-liftable Dehn twist into the largest power that does lift and the single power that doesn't **/
    public List<DehnTwist> decomp() {
        if (this.is_liftable()) {
            throw new InvalidParameterException("This does not need to be decomposed");
        }

        DehnTwist liftable_part;
        DehnTwist nonliftable_part;


        if (exp < 0) {
            nonliftable_part = new DehnTwist(this.identifier, -1);
            liftable_part = new DehnTwist(this.identifier, exp + 1);
        } else {
            nonliftable_part = new DehnTwist(this.identifier, 1);
            liftable_part = new DehnTwist(this.identifier, exp - 1);
        }

        List<DehnTwist> ret_val = new ArrayList<>();

        if (liftable_part.getExp() != 0) {
            ret_val.add(liftable_part);
        }

        ret_val.add(nonliftable_part);
        return ret_val;


        
    }
    // TODO: override the toString
    @Override
    public String toString() {
        return "D(" + this.identifier.getFirst() + ", " + this.identifier.getSecond() + ")^" + this.getExp();
    }

}
