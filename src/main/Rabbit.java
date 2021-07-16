package main;

import java.security.InvalidParameterException;
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
        createMap();
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
            //z
            generators.add(generators.get(0).conjugate(temp));
            //b
            generators.add(generators.get(1).conjugate(temp));
            //w
            generators.add(generators.get(2).conjugate(temp));
            //c^2
            generators.add(generators.get(3).conjugate(temp));
        }

        return generators;
    }

    public int getEarCount() {
        return earCount;
    }

    /** generates a random Dehn Twist. **/
    public DehnTwist genRandomTwist() {
        int a = resident_random.nextInt(earCount);
        int b = resident_random.nextInt(earCount);

        if ((a - b) % earCount != 0) {
            return new DehnTwist(a, b, resident_random.nextInt(10));
        } else {
            return new DehnTwist(a, b + 1, resident_random.nextInt(10));
        }
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
    public MappingClass lift(MappingClass input) {
        // check if it's actually liftable
        if (input.isLiftable()) {
            return liftableHelper(input);
        } else if (input.getCoset() == 2) {
            // TODO: not hardcode
            DehnTwist c = new DehnTwist(2, 4, 1);
            input.append(0, c.inverse());
            return liftableHelper(input).multi(new MappingClass(c));
        } else if (input.getCoset() == 3) {
            DehnTwist x = new DehnTwist(3, 4, 1);
            input.append(0, x.inverse());
            return liftableHelper(input).multi(new MappingClass(x));
        } else if (input.getCoset() == 6) {
            DehnTwist x = new DehnTwist(3, 4, 1);
            DehnTwist c = new DehnTwist(2, 4, 1);
            input.append(0, c.inverse());
            input.append(0, x.inverse());
            return liftableHelper(input).multi(new MappingClass(c)).multi(new MappingClass(x));
        } else {
            throw new InvalidParameterException("UH OH this is not a valid coset I don't know what to borrow by");
        }
    }

    private MappingClass liftableHelper(MappingClass input) {
        input = input.simplify();

        List<MappingClass> lifted_mc = new ArrayList<>();
        List<MappingClass> before_lifting = input.parser().comb();
        System.out.println(before_lifting.size());
        for (MappingClass mc : before_lifting) {
            // if it's in the map
            if (liftingMap.containsKey(mc)) {
                lifted_mc.add(liftingMap.get(mc));
            } else if (liftingMap.containsKey(mc.inverse())) {
                // if its inverse is in the map
                lifted_mc.add(liftingMap.get(mc.inverse()).inverse());
            } else {
                // uh oh
                System.out.println(mc);
                throw new InvalidParameterException("UH OH the generator is not in the map *gasp*");
            }
        }

        return MappingClass.multiAll(lifted_mc);
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
                        lift.put(mc.conjugate(temp), new MappingClass(new DehnTwist(1, 2, 0)));
                        //Is there a better way to do this?
                    } else {
                        lift.put(mc.conjugate(temp),
                                new MappingClass(new DehnTwist(first+1, second+1, 1)).conjugate(new MappingClass(new DehnTwist(first + 1, k+1, -1))));
                    }
                }
            }
        }

        for (int i = 2; i < earCount + 1; i++) {
            for (int k = 2; k < i; k++) {
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
            lift.put(generators.get(0).conjugate(temp), new MappingClass(new DehnTwist(1,2,0)));
            lift.put(generators.get(1).conjugate(temp), new MappingClass(new DehnTwist(1,2,0)));
            MappingClass y = new MappingClass(new DehnTwist(1, 4, 1));
            MappingClass b = new MappingClass(new DehnTwist(1, 3, 1));
            lift.put(generators.get(2).conjugate(temp), new MappingClass(new DehnTwist(3, 4, 1)).conjugate(y).conjugate(b));
            lift.put(generators.get(3).conjugate(temp), b);
        }

        this.liftingMap = lift;
    }

    public List<MappingClass> getGenerators() {
        return this.generators;
    }
}
