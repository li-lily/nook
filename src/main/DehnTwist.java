package main;


import java.io.InvalidObjectException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class DehnTwist {
    private Pair identifier;
    private int exp;
    private char nickname = 'o';
    private int earCount;

    /** Constructor takes in two points and an exponent, plus a rabbit to determine ears **/
    public DehnTwist(int first, int second, int exp, Rabbit r) {

        this.exp = exp;

        if (first == second) {
            throw new InvalidParameterException("The Dehn Twist input does not surround two distinct points.");
        } else if (first < second) {
            this.identifier = new Pair(first, second);
        } else {
            this.identifier = new Pair(second, first);
        }

        this.earCount = r.earCount;

        if (r.earCount == 3) {
            if (identifier.getFirst() == 1) {
                if (identifier.getSecond() == 2) {
                    nickname = 'z';
                } else if (identifier.getSecond() == 3) {
                    nickname = 'b';
                } else if (identifier.getSecond() == 4) {
                    nickname = 'y';
                }
            } else if (identifier.getFirst() == 2) {
                if (identifier.getSecond() == 3) {
                    nickname = 'w';
                } else if (identifier.getSecond() == 4) {
                    nickname = 'c';
                }
            } else if (identifier.getFirst() == 3) {
                nickname = 'x';
            }
        }
    }

//    /** Constructor takes in two points and an exponent, plus a rabbit to determine ears **/
//    public DehnTwist(int first, int second, int exp, int earCount) {
//
//        this.exp = exp;
//
//        if (first == second) {
//            throw new InvalidParameterException("The Dehn Twist input does not surround two distinct points.");
//        } else if (first < second) {
//            this.identifier = new Pair(first, second);
//        } else {
//            this.identifier = new Pair(second, first);
//        }
//
//        this.earCount = earCount;
//
//        if (earCount == 3) {
//            if (identifier.getFirst() == 1) {
//                if (identifier.getSecond() == 2) {
//                    nickname = 'z';
//                } else if (identifier.getSecond() == 3) {
//                    nickname = 'b';
//                } else if (identifier.getSecond() == 4) {
//                    nickname = 'y';
//                }
//            } else if (identifier.getFirst() == 2) {
//                if (identifier.getSecond() == 3) {
//                    nickname = 'w';
//                } else if (identifier.getSecond() == 4) {
//                    nickname = 'c';
//                }
//            } else if (identifier.getFirst() == 3) {
//                nickname = 'x';
//            }
//        }
//    }

    /** Constructor takes in two points and an exponent with default of 3 ears **/
    public DehnTwist(int first, int second, int exp) {

        this.exp = exp;
        // warning: HARDCODED
        this.earCount = Rabbit.defaultEarCount;

        if (first == second) {
            throw new InvalidParameterException("The Dehn Twist input does not surround two distinct points.");
        } else if (first < second) {
            this.identifier = new Pair(first, second);
        } else {
            this.identifier = new Pair(second, first);
        }

        if (this.earCount == 3) {
            if (identifier.getFirst() == 1) {
                if (identifier.getSecond() == 2) {
                    nickname = 'z';
                } else if (identifier.getSecond() == 3) {
                    nickname = 'b';
                } else if (identifier.getSecond() == 4) {
                    nickname = 'y';
                }
            } else if (identifier.getFirst() == 2) {
                if (identifier.getSecond() == 3) {
                    nickname = 'w';
                } else if (identifier.getSecond() == 4) {
                    nickname = 'c';
                }
            } else if (identifier.getFirst() == 3) {
                nickname = 'x';
            }
        }

    }

    /** Extra constructor takes in identifier and an exponent **/
    public DehnTwist(Pair identifier, int exp) {

        this.exp = exp;
        this.identifier = identifier;
        this.earCount = Rabbit.defaultEarCount;
        if (identifier.getEarCount() == 3) {
            if (identifier.getFirst() == 1) {
                if (identifier.getSecond() == 2) {
                    nickname = 'z';
                } else if (identifier.getSecond() == 3) {
                    nickname = 'b';
                } else if (identifier.getSecond() == 4) {
                    nickname = 'y';
                }
            } else if (identifier.getFirst() == 2) {
                if (identifier.getSecond() == 3) {
                    nickname = 'w';
                } else if (identifier.getSecond() == 4) {
                    nickname = 'c';
                }
            } else if (identifier.getFirst() == 3) {
                nickname = 'x';
            }
        }
    }

    public Pair getidentifier() {
        return identifier;
    }

    public int getExp() {
        return exp;
    }

    public boolean isLiftable() {
        return !((identifier.getSecond() == this.earCount + 1) && (exp % 2 != 0));
    }

    /** Decomposes non-liftable Dehn twist into the largest power that does lift and the single power that doesn't **/
    public List<DehnTwist> decomp() {
        if (this.isLiftable()) {
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

    /** Decides whether two Dehn Twists commute with one another **/
    public boolean commutesWith(DehnTwist t) {
        if (this.getidentifier().equals(t.getidentifier())) {
            return true;
        }
        // if "this" contains the smallest indexed point
        if (this.getidentifier().getFirst() < t.getidentifier().getFirst()) {
            return this.getidentifier().getSecond() < t.getidentifier().getFirst();
        } else {
            return t.getidentifier().getSecond() < this.getidentifier().getFirst();
        }
    }

    /** Returns the inverse of the current Dehn Twist **/
    public DehnTwist inverse() {
        return new DehnTwist(this.identifier, -this.exp);
    }

    @Override
    public boolean equals(Object t) {

        // If the object is compared with itself then return true
        if (t == this) {
            return true;
        }

        if (!(t instanceof DehnTwist)) {
            return false;
        }

        DehnTwist s = (DehnTwist) t;

        return (this.getExp() == s.getExp() && this.getidentifier().equals(s.getidentifier()));
    }

    @Override
    public String toString() {
        if (nickname != 'o') {
            return "D(" + nickname + ")^" + this.getExp();
        }
        return "D(" + this.identifier.getFirst() + ", " + this.identifier.getSecond() + ")^" + this.getExp();
    }

}
