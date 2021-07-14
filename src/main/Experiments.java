package main;

import java.util.ArrayList;
import java.util.List;

public class Experiments {
    private static List<MappingClass> nucleus;
    private static boolean cond() {
        // TODO: Edit as needed
        return true;
    }

    private static MappingClass runExp(MappingClass MC) {
        int prev_length = MC.getLength();
        while (cond()) {
            //MC = MC.lift();

        }

        return MC;
    }

    public static void main(String args[]) {
        // 3 ears
        Rabbit r = new Rabbit();

        for (int i = 0; i < 100; i++) {
            MappingClass test = r.genRandomWord(20);

            // lift
            nucleus = new ArrayList<>();
            nucleus.add(runExp(test));
        }
        System.out.println(nucleus);
    }
    }

