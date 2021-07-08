package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static java.lang.Math.abs;

public class Rabbit {
    public static int earCount = 3;
    private Random resident_random;
    private List<MappingClass> generators;

    public Rabbit(int earCount) {
        this.resident_random = new Random(200);
        this.earCount = earCount;
        this.generators = createGenerators();
    }

    /** Constructor with default ears **/
    public Rabbit() {
        this.resident_random = new Random(200);
        this.generators = createGenerators();
    }

    public List<MappingClass> createGenerators() {
        // TODO: write this function
        return null;
    }

    public int getEarCount() {
        return earCount;
    }

    /** generates a random Dehn Twist. **/
    public DehnTwist genRandomTwist() {
        return new DehnTwist(resident_random.nextInt(earCount), resident_random.nextInt(earCount), resident_random.nextInt(10));
    }

    /** Generates a random Mapping Class**/
    public MappingClass genRandomWord() {
        int num_terms = abs(resident_random.nextInt());
        List<DehnTwist> twist_list = new ArrayList<>();

        while (num_terms > 0) {
            DehnTwist next = genRandomTwist();
            twist_list.add(next);
            num_terms--;
        }

        return new MappingClass(twist_list);
    }

    /** THE MAP THAT REALIZES THE VIRTUAL ENDOMORPHISM **/
    public MappingClass lift(List<MappingClass> input) {
        // TODO: implement

        return null;
    }
}
