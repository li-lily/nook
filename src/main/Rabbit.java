package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static java.lang.Math.abs;

public class Rabbit {
    public int earCount = 3;
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
        List<MappingClass> generators = new ArrayList<>();
        //First add twists that do lift
        for (int j = 1; j < earCount + 1; j++) {
            for (int i = 1; i < j; i++) {
                generators.add(new MappingClass(new DehnTwist(i,j,1)));
            }
        }
        //Now add squares of twists that don't lift
        for (int i = 1; i < earCount; i++) {
            generators.add(new MappingClass(new DehnTwist(i,earCount + 1,2)));
        }

        //Adding commutators of Type II twists
        for (int i = 1; i < earCount; i++) {
            MappingClass temp1 = new MappingClass(new DehnTwist(i,earCount + 1,1));
            for (int j = 1; j < i; j++) {
                MappingClass temp2 = new MappingClass(new DehnTwist(j, earCount + 1, 1));
                generators.add(temp1.commutator(temp2));
            }
        }

        //Now we need to add conjugates
        //First we add the conjugates of the single generators
        for (int i = 0; i < (earCount)*(earCount - 1)*(1./2.) + (earCount - 1); i++) {
            for (int k = 1; k < earCount + 1; k++) {
                MappingClass temp = new MappingClass(new DehnTwist(k, earCount + 1, 1));
                if (!generators.get(i).commutesWith(temp)) {
                    generators.add(generators.get(i).conjugate(temp));
                }
            }
        }

        //TODO: Going to redo the above code using new constructor and conjugator function


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
        List<MappingClass> lifted_mc;
        //TODO: Implement
        return null;
    }
}
