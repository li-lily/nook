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
                List<DehnTwist> twist = new ArrayList<>();
                twist.add(new DehnTwist(i,j,1));
                generators.add(new MappingClass(twist));
            }
        }
        //Now add squares of twists that don't lift
        for (int i = 1; i < earCount; i++) {
            List<DehnTwist> twist = new ArrayList<>();
            twist.add(new DehnTwist(i,earCount + 1,2));
            generators.add(new MappingClass(twist));
        }

        //Now we need to add conjugates
        for (int j = 1; j < earCount + 1; j++) {
            for (int i = 1; i < j; i++) {
                for (int k = 1; k < earCount + 1; k++) {
                    DehnTwist elt = new DehnTwist(i, j, 1);
                    DehnTwist conjugator = new DehnTwist(i, j, 1);
                    if (!elt.commutesWith(conjugator)) {
                        List<DehnTwist> twist = new ArrayList<>();
                        twist.add(conjugator.inverse());
                        twist.add(elt);
                        twist.add(conjugator);
                    }
                }
            }
        }


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

    /** Multiplies two mapping classes **/
    public MappingClass multi(MappingClass mc1, MappingClass mc2) {
        List<DehnTwist> newTwists = new ArrayList<>(mc1.getWord());
        newTwists.addAll(mc2.getWord());
        MappingClass product = new MappingClass(newTwists);
        return product.concatenate();
    }

    /** THE MAP THAT REALIZES THE VIRTUAL ENDOMORPHISM **/
    public MappingClass lift(List<MappingClass> input) {
        List<MappingClass> lifted_mc;
        //TODO: Implement
        return null;
    }
}
