package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MappingClass {
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
    public MappingClass simplify() {
        // TODO: SET LENGTH
        List<DehnTwist> newList = new ArrayList<>();
        MappingClass temp = new MappingClass(this.getWord());
        while (!temp.isSimplified()) {
            int i = 0;
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

            temp = new MappingClass(newList);
            this.word = newList;
        }

        return temp;
    }

    /** Checks if the word of a mapping class can still be simplified further **/
    private boolean isSimplified() {
        for (int i = 0; i < this.word.size() - 1; i++) {
            DehnTwist current = this.word.get(i);
            DehnTwist next = this.word.get(i+1);
            if (current.getidentifier().equals(next.getidentifier())) {
                // checks for the same Dehn twist consecutive to each other
                return false;
            } else if (current.getExp() == 0) {
                // checks for 0 exponents
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
        return product.simplify();
    }

    /** Go CS **/
    public MappingClass conjugate(MappingClass conjugator) {
        MappingClass temp = this.multi(conjugator.inverse());
        return conjugator.multi(temp);
    }

    /** Go CS Part 2 **/
    public MappingClass commutator(MappingClass other_mc) {
        return other_mc.conjugate(this).multi(other_mc.inverse());
    }

    /** Function defining when two mapping classes commute**/
    public boolean commutesWith(MappingClass m) {
        if (this.equals(m)) {
            return true;
        }
        for (int i = 0; i < this.word.size(); i++) {
            for (int j = 0; j < m.word.size(); j++) {
                if (!this.word.get(i).commutesWith(m.word.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Breaks down Mapping Class into its generators **/
    public List<MappingClass> comb() {

    }

    private List<MappingClass> liftableFactor(MappingClass m) {
        List<DehnTwist> conjugator = new ArrayList<>();
        List<MappingClass> factoredMC = new ArrayList<>();
        for (int i = 0; i < m.getWord().size(); i++) {
            if (m.getWord().get(i).isLiftable()) {
                MappingClass conjugatorMC = new MappingClass(conjugator).inverse();
                MappingClass conjugatedLiftable = m.conjugate(conjugatorMC);
                m.getWord().remove(i);
                factoredMC.add(conjugatedLiftable);
                factoredMC.addAll(liftableFactor(m));
                return factoredMC;
            } else {
                conjugator.add(m.getWord().get(i));
            }
        }

        factoredMC.add(m);
        return factoredMC;
    }

    private List<MappingClass> nonliftableFactor(MappingClass m) {

    }

    public List<DehnTwist> getWord() {
        return this.word;
    }

    @Override
    public boolean equals(Object t) {

        // If the object is compared with itself then return true
        if (t == this) {
            return true;
        }

        if (!(t instanceof MappingClass)) {
            return false;
        }

        MappingClass s = (MappingClass) t;

        if (this.getWord().size() != s.getWord().size()) {
            return false;
        }

        for (int i = 0; i < this.word.size(); i++) {
            DehnTwist current_s = s.word.get(i);
            DehnTwist current_self = this.word.get(i);
            if (!current_s.equals(current_self)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        String output = "Mapping Class: ";
        for (DehnTwist twist : this.getWord()) {
            output += twist.toString();
        }
        return output;
    }
}
