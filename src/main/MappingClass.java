package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MappingClass extends Rabbit {
    // a binary encoding of the coset
    // TODO: Encode coset as product of primes, the product of two cosets is lcm/gcf
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

    public MappingClass (DehnTwist twist) {
        List<DehnTwist> twist_list = new ArrayList<>();
        twist_list.add(twist);
        this.word = twist_list;
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

    /** Takes in a mapping class and concatenates its list of Dehn Twists **/
    public MappingClass concatenate() {
        // TODO: implement
        return null;
    }

    /** Breaks down Mapping Class into its generators **/
    public void comb() {
        // TODO: implement big boi
        // will need to access generators
    }

    public List<DehnTwist> getWord() {
        return this.word;
    }
}
