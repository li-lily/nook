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
        List<DehnTwist> newList = new ArrayList<>();
        MappingClass temp = new MappingClass(this.getWord());
        int i = 0;
        while (!temp.isSimplified()) {
            while (i < temp.getWord().size()) {
                int totalExp = temp.getWord().get(i).getExp();
                while (i != temp.getWord().size() - 1 && temp.getWord().get(i).getidentifier().equals(temp.getWord().get(i+1).getidentifier())) {
                    totalExp += temp.getWord().get(i+1).getExp();
                    i++;
                }
                if (totalExp != 0) {
                    newList.add(new DehnTwist(temp.getWord().get(i).getidentifier(), totalExp));
                }
                i++;
            }
        }

        return temp;
    }

    /** Checks if the word of a mapping class can still be simplified further **/
    private boolean isSimplified() {
        for (int i = 0; i < this.word.size() - 1; i++) {
            if (this.word.get(i).getidentifier().equals(this.word.get(i+1).getidentifier())) {
                return false;
            }
        }
        return true;
    }

    /** Returns the inverse of a mapping class **/
    public MappingClass inverse() {
        List<DehnTwist> inverseList = new ArrayList<>();
        for (int i = 0; i < this.word.size(); i++) {
            inverseList.add(0, this.word.get(i).inverse());
        }
        return new MappingClass(inverseList);
    }

    /** Multiplies the current mapping class and another mapping class **/
    public MappingClass multi(MappingClass other_mc) {
        List<DehnTwist> newTwists = new ArrayList<>(this.getWord());
        newTwists.addAll(other_mc.getWord());
        MappingClass product = new MappingClass(newTwists);
        return product.concatenate();
    }

    /** Go CS **/
    public MappingClass conjugate(MappingClass conjugator) {
        return conjugator.multi(this.multi(conjugator.inverse()));
    }

    /** Go CS Part 2 **/
    public MappingClass commutator(MappingClass other_mc) {
        return (this.conjugate(other_mc)).multi(other_mc.inverse());
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
