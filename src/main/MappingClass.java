package main;

import java.util.HashMap;
import java.util.List;

public class MappingClass extends Rabbit {
    // a binary encoding of the coset
    private int coset;
    private int length;
    private List<DehnTwist> word;
    private HashMap<List<DehnTwist>, Boolean> gen_count;

    public MappingClass (List<DehnTwist> word) {
        this.word = word;
        this.gen_count = parser();
        this.coset = findCoset(word);
        this.length = 0; //TODO: DONT FORGET TO SET LENGTH
    }

    public boolean isLiftable() {
        return (coset == 1);
    }

    private HashMap<List<DehnTwist>, Boolean> parser() {
        // returns the result of breaking into generators
        // ALSO MAKE SURE TO SET THE LENGTH
        //TODO: implement
        return null;
    }

    private int findCoset(List<DehnTwist> word) {
        // use the parsed hashmap to obtain coset
        // TODO: implement
        return 0;
    }

    /** Breaks down Mapping Class into its generators **/
    public void comb() {
        // TODO: implement big boi
        // will need to access generators
    }
}
