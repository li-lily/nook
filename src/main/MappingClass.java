package main;

import java.util.*;
import java.util.stream.IntStream;

public class MappingClass {
    // a binary encoding of the coset
    // TODO: Encode coset as product of primes, the product of two cosets is lcm/gcf
    private int coset;
    private int length;
    private static int[] primes;
    private static boolean genPrimes = false;
    private List<DehnTwist> word;
    private HashMap<List<DehnTwist>, Boolean> gen_count;

    public MappingClass (List<DehnTwist> word) {
        if (!genPrimes) {
            primes = primeList();
            genPrimes = true;
        }
        this.word = word;
        this.gen_count = parser();
        this.coset = findCoset(word);
        this.length = 0; //TODO: DONT FORGET TO SET LENGTH
    }

    public MappingClass (DehnTwist twist) {
        if (!genPrimes) {
            primes = primeList();
            genPrimes = true;
        }
        List<DehnTwist> twist_list = new ArrayList<>();
        twist_list.add(twist);
        this.word = twist_list;
        this.gen_count = parser();
        this.coset = findCoset(word);
        this.length = 0; //TODO: DONT FORGET TO SET LENGTH
    }

    private int[] primeList() {
        int[] primes = new int[Rabbit.defaultEarCount + 1];
        //Padded list of primes with a 1
        primes[0] = 1;
        int i = 0;
        int j = 1;
        while (i < Rabbit.defaultEarCount) {
            if (isPrime(j)) {
                primes[i+1] = j;
                i++;
            }
            j++;
        }
        return primes;
    }

    /**We stole this**/
    private boolean isPrime(int number) {
        return number > 1
                && IntStream.rangeClosed(2, (int) Math.sqrt(number))
                .noneMatch(n -> (number % n == 0));
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

    /**Assigns the coset of the mapping class**/
    private void findCoset() {
        HashMap<Integer, Integer> cosets = new HashMap<>();
        for (DehnTwist d : word) {
            if (!d.isLiftable()) {
                int coset_name = d.getidentifier().getFirst();
                if (cosets.containsKey(coset_name)) {
                    cosets.put(coset_name, 1 - cosets.get(coset_name));
                } else  {
                    cosets.put(coset_name, 1);
                }
            }
        }

        int coset = 1;
        for (int i : cosets.keySet()) {
            if (cosets.get(i) != 0) {
                coset *= primes[i];
            }
        }

        this.coset = coset;
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
        // make the container for all the generators in the end
        List<MappingClass> result = new ArrayList<>();

        // separate the word into conjugated liftable portion and nonliftable portion
        List<MappingClass> separatedWord = this.liftableFactor();
        // pull out the factor with purely nonliftable generators
        int lastIndex = separatedWord.size() - 1;
        MappingClass nonliftableFactor = separatedWord.remove(lastIndex);
        // parse that factor into a list of generators
        List<MappingClass> nonliftableGenerator = nonliftableFactor.nonliftableFactor();


        // parse each of the conjugated liftable factors
        for (MappingClass MC : separatedWord) {
            // extract the conjugating element
            int MCLength = MC.getWord().size();
            int mid = Math.floorDiv(MCLength, 2);

            // make a mapping class containing just the liftable element
            MappingClass liftableElem = new MappingClass(MC.getWord().get(mid));

            // make the conjugating element a new list
            List<DehnTwist> conjugatingTwists = new ArrayList<>();
            // complete the list of the conjugating element by adding in twists until the middle
            for (int i = 0; i < mid; i++) {
                conjugatingTwists.add(MC.getWord().get(i));
            }
            MappingClass conjugatingElem = new MappingClass(conjugatingTwists);


            // process the conjugating element through the nonliftable factor function
            if (conjugatingElem.isLiftable()) {
                // add in the generators for the conjugating element
                List<MappingClass> conjGenerators = conjugatingElem.nonliftableFactor();
                result.addAll(conjGenerators);
                // add the conjugated liftable term
                result.add(liftableElem);
                // add the inverse of the conjugating element
                result.addAll(invertAll(conjGenerators));
            } else {
                // TODO: implement
                // first get the right coset

                // depending on the coset, add in the correct terms and
            }
        }

        result.addAll(nonliftableGenerator);
        return result;
    }

    private static List<MappingClass> invertAll(List<MappingClass> conj) {
        List<MappingClass> inverted = new ArrayList<>();
        for (MappingClass term : conj) {
            inverted.add(0, term.inverse());
        }
        return inverted;
    }

    public List<MappingClass> liftableFactor() {
        List<DehnTwist> conjugator = new ArrayList<>();
        List<MappingClass> factoredMC = new ArrayList<>();

        // iterate through all the Dehn twists looking for liftable items
        for (int i = 0; i < this.getWord().size(); i++) {
            if (this.getWord().get(i).isLiftable()) {
                MappingClass conjugatorMC = new MappingClass(conjugator);
                MappingClass liftable = new MappingClass(this.getWord().get(i));
                MappingClass conjugatedLiftable = liftable.conjugate(conjugatorMC);

                // the rest of the word is the same except without the liftable element
                this.getWord().remove(i);
                factoredMC.add(conjugatedLiftable);
                factoredMC.addAll(this.liftableFactor());
                return factoredMC;
            } else {
                // if the current thing is not liftable, we are simply building up the conjugator
                conjugator.add(this.getWord().get(i));
            }
        }

        factoredMC.add(this);
        return factoredMC;
    }

    private List<MappingClass> nonliftableFactor() {
        // TODO: implement
        return null;
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
