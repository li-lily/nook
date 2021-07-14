package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static java.lang.Math.abs;

public class Rabbit {
    public int earCount = 3;
    public static int defaultEarCount = 3;
    private Random resident_random;
    private List<MappingClass> generators;
    private HashMap<MappingClass,MappingClass> liftingMap;

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
        for (int i = 2; i < earCount + 1; i++) {
            generators.add(new MappingClass(new DehnTwist(i,earCount + 1,2)));
        }

        //Adding commutators of Type II twists
        for (int i = 2; i < earCount + 1; i++) {
            MappingClass temp1 = new MappingClass(new DehnTwist(i,earCount + 1,1));
            for (int j = 2; j < i; j++) {
                MappingClass temp2 = new MappingClass(new DehnTwist(j, earCount + 1, 1));
                generators.add(temp1.commutator(temp2));
            }
        }

        //Now we need to add conjugates
        //First we add the conjugates of the single generators
        for (int i = 0; i < (earCount*(1./2.))*(earCount - 1) + (earCount - 1); i++) {
            for (int k = 2; k < earCount + 1; k++) {
                MappingClass temp = new MappingClass(new DehnTwist(k, earCount + 1, 1));
                if (!generators.get(i).commutesWith(temp)) {
                    generators.add(generators.get(i).conjugate(temp));
                }
            }
        }

        //Finally we add conjugates of commutators
        for (int i = 2; i < earCount + 1; i++) {
            MappingClass temp1 = new MappingClass(new DehnTwist(i,earCount + 1,1));
            for (int j = 2; j < i; j++) {
                for (int k = 2; k < earCount + 1; k++) {
                    MappingClass temp2 = new MappingClass(new DehnTwist(j, earCount + 1, 1));
                    MappingClass con = new MappingClass(new DehnTwist(k, earCount + 1, 1));
                    generators.add(temp1.commutator(temp2).conjugate(con));
                }

            }
        }

        if (this.earCount == 3) {
            MappingClass temp = new MappingClass(new DehnTwist(2, earCount + 1, 1));
            temp.append(new DehnTwist(3, earCount + 1, 1));
            generators.add(generators.get(1).conjugate(temp));
            generators.add(generators.get(2).conjugate(temp));
        }

        return generators;
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
        return genRandomWord(num_terms);
    }

    /** Generates a random Mapping Class**/
    public MappingClass genRandomWord(int length) {
        int num_terms = length;
        List<DehnTwist> twist_list = new ArrayList<>();

        while (num_terms > 0) {
            DehnTwist next = genRandomTwist();
            twist_list.add(next);
            num_terms--;
        }

        return new MappingClass(twist_list);
    }

    /** THE MAP THAT REALIZES THE VIRTUAL ENDOMORPHISM **/
    /** Note that this function heavily relies on the order the generators were created **/
    public MappingClass lift(List<MappingClass> input) {
        List<MappingClass> lifted_mc = new ArrayList<>();
        for (MappingClass mc : input) {
            lifted_mc.add(liftingMap.get(mc));
        }

        return null;

    }

    //TODO: Implement this
    private void createMap() {
        HashMap<MappingClass, MappingClass> lift = new HashMap<>();
        for (int j = 1; j < earCount + 1; j++) {
            for (int i = 1; i < j; i++) {
                lift.put(new MappingClass(new DehnTwist(i,j,1)), new MappingClass(new DehnTwist(i+1, j+1, 1)));
            }
        }
        //Now add squares of twists that don't lift
        for (int i = 2; i < earCount + 1; i++) {
            lift.put(new MappingClass(new DehnTwist(i,earCount + 1,2)), new MappingClass(new DehnTwist(1, i+1, 1)));
        }

        //Adding commutators of Type II twists
        /*for (int i = 2; i < earCount + 1; i++) {
            MappingClass temp1 = new MappingClass(new DehnTwist(i,earCount + 1,1));
            for (int j = 2; j < i; j++) {
                MappingClass temp2 = new MappingClass(new DehnTwist(j, earCount + 1, 1));
                generators.add(temp1.commutator(temp2));
            }
        }*/

        //Commutators for 3-eared case
        if (earCount == 3) {
            lift.put(new MappingClass(new DehnTwist(3, 4, 1)).commutator(new MappingClass(new DehnTwist(2, 4, 1))),
                    new MappingClass(new DehnTwist(3, 4, -1)));
        }


        //Now we need to add conjugates
        //First we add the conjugates of the single generators
        for (int i = 0; i < (earCount*(1./2.))*(earCount - 1); i++) {
            for (int k = 2; k < earCount + 1; k++) {
                MappingClass temp = new MappingClass(new DehnTwist(k, earCount + 1, 1));
                if (!generators.get(i).commutesWith(temp)) {
                    MappingClass mc = generators.get(i);
                    int first = mc.getWord().get(0).getidentifier().getFirst();
                    int second = mc.getWord().get(0).getidentifier().getSecond();
                    if (first == k || second == k) {
                        lift.put(mc.conjugate(temp), new MappingClass(new DehnTwist(1, 1, 0)));
                        //Is there a better way to do this?
                    } else {
                        lift.put(mc.conjugate(temp),
                                new MappingClass(new DehnTwist(first+1, second+1, 1)).conjugate(new MappingClass(new DehnTwist(first + 1, k+1, -1))));
                    }
                }
            }
        }

        for (int i = 1; i < earCount + 1; i++) {
            for (int k = 1; k < i; k++) {
                lift.put(new MappingClass(new DehnTwist(i, earCount + 1 , 2)).conjugate(new MappingClass(new DehnTwist(k, earCount + 1, 1))),
                        new MappingClass(new DehnTwist(1, k, 1)));
            }
            for (int k = i + 1; k < earCount + 1; k++) {
                lift.put(new MappingClass(new DehnTwist(i, earCount + 1 , 2)).conjugate(new MappingClass(new DehnTwist(k, earCount + 1, 1))),
                        new MappingClass(new DehnTwist(1, i + 1, 1)).conjugate(new MappingClass(new DehnTwist(i+1, k+1, -1))));
            }
        }

        //Finally we add conjugates of commutators
        /*for (int i = 2; i < earCount + 1; i++) {
            MappingClass temp1 = new MappingClass(new DehnTwist(i,earCount + 1,1));
            for (int j = 2; j < i; j++) {
                for (int k = 2; k < earCount + 1; k++) {
                    MappingClass temp2 = new MappingClass(new DehnTwist(j, earCount + 1, 1));
                    MappingClass con = new MappingClass(new DehnTwist(k, earCount + 1, 1));
                    generators.add(temp1.commutator(temp2).conjugate(con));
                }

            }
        }*/

        //Conjugates for commutators for 3-eared case
        if (this.earCount == 3) {

        }

        if (this.earCount == 3) {
            MappingClass temp = new MappingClass(new DehnTwist(2, earCount + 1, 1));
            temp.append(new DehnTwist(3, earCount + 1, 1));
            lift.put(generators.get(1).conjugate(temp), new MappingClass(new DehnTwist(1,1,0)));
            MappingClass y = new MappingClass(new DehnTwist(1, 4, 1));
            MappingClass b = new MappingClass(new DehnTwist(1, 3, 1));
            lift.put(generators.get(2).conjugate(temp), new MappingClass(new DehnTwist(3, 4, 1)).conjugate(y).conjugate(b));
        }

        this.liftingMap = lift;
    }

    public List<MappingClass> getGenerators() {
        return this.generators;
    }
}
