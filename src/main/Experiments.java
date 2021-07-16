package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Experiments {
    private static List<MappingClass> nucleus;
    private static boolean cond() {
        // TODO: Edit as needed
        return true;
    }

    public static void main(String args[]) {
        // 3 ears
        // make all the twists
        Rabbit r = new Rabbit();
        DehnTwist x = new DehnTwist(3, 4, 1);
        DehnTwist c = new DehnTwist(2, 4, 1);
        DehnTwist z = new DehnTwist(2, 1, 1);
        DehnTwist b = new DehnTwist(1, 3, 1);
        DehnTwist w = new DehnTwist(2, 3, 1);
        DehnTwist y = new DehnTwist(1, 4, 1);
        List<DehnTwist> everything = new ArrayList<>();

        // make a list of all length  less than 3 twists
        everything.add(x);
        everything.add(w);
        everything.add(z);
        everything.add(b);
        everything.add(c);
        everything.add(y);
        everything.add(x.inverse());
        everything.add(w.inverse());
        everything.add(z.inverse());
        everything.add(b.inverse());
        everything.add(c.inverse());

        Map<MappingClass, Boolean> three_twists = new HashMap<>();


        for (DehnTwist t1 : everything) {
            for (DehnTwist t2 : everything) {
                for (DehnTwist t3 : everything) {
                    List<DehnTwist> test = new ArrayList<>();
                    test.add(t1);
                    test.add(t2);
                    test.add(t3);
                    MappingClass test_simplified = new MappingClass(test);
                    test_simplified.simplify();
                    three_twists.put(test_simplified, true);
                }
            }
        }

        for (DehnTwist t4 : everything) {
            for (DehnTwist t5 : everything) {
                List<DehnTwist> test = new ArrayList<>();
                test.add(t4);
                test.add(t5);
                MappingClass test_simplified = new MappingClass(test);
                test_simplified.simplify();
                three_twists.put(test_simplified, true);            }
        }

        for (DehnTwist t6 : everything) {
            List<DehnTwist> test = new ArrayList<>();
            test.add(t6);
            MappingClass test_simplified = new MappingClass(test);
            test_simplified.simplify();
            three_twists.put(test_simplified, true);
        }

        everything.remove(y);

        for (DehnTwist t1 : everything) {
            for (DehnTwist t2 : everything) {
                for (DehnTwist t3 : everything) {
                    for (DehnTwist t4 : everything){
                        List<DehnTwist> test = new ArrayList<>();
                        test.add(t1);
                        test.add(t2);
                        test.add(t3);
                        test.add(t4);
                        MappingClass MC = new MappingClass(test);
                        System.out.println("This is what we're starting with: " + MC);
                        int counter = 0;
                        MappingClass shortest_MC= new MappingClass(test);
                        while (MC.getLength() > 3 && counter < 40 && !three_twists.containsKey(MC)) {
                            MappingClass temp = r.lift(MC);
                            if (temp.getLength() < shortest_MC.getLength()) {
                                shortest_MC = temp;
                            }
                            MC = r.lift(MC);
                            counter++;
                        }

                        if (three_twists.containsKey(MC)) {
                            shortest_MC = MC;
                        }

                        if (shortest_MC.getLength() == 4 && !three_twists.containsKey(MC)) {
                            System.out.println("NO CHANGE: This is the final result: " + MC);

                        } else {
                            System.out.println("GOT SHORTER: This is the final result: " + shortest_MC);
                        }
                    }
                }
            }
        }
    }
    }

