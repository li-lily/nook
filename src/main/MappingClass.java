package main;

import java.security.InvalidParameterException;
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
    private boolean identity = false;
    //private HashMap<List<DehnTwist>, Boolean> gen_count;

    public MappingClass (List<DehnTwist> word) {
        if (word.isEmpty()) {
            this.identity = true;
            this.word = word;
            this.coset = 1;
        } else {
            if (!genPrimes) {
                primes = primeList();
                genPrimes = true;
            }
            this.word = word;
            //this.gen_count = parser();
            preprocessing();
            findCoset();
            this.length = 0; //TODO: DONT FORGET TO SET LENGTH
        }

    }

    public MappingClass (DehnTwist twist) {
        if (!genPrimes) {
            primes = primeList();
            genPrimes = true;
        }
        List<DehnTwist> twist_list = new ArrayList<>();
        twist_list.add(twist);
        this.word = twist_list;
        //this.gen_count = parser();
        preprocessing();
        findCoset();
        this.length = 0; //TODO: DONT FORGET TO SET LENGTH
    }

    private void preprocessing() {
        // make a new list to prepare for copying down the same dehn twists
        List<DehnTwist> replaced_twists = new ArrayList<>();

        int k = 0;
        while(k < this.word.size() - 5) {
            if (!identityChecker(k)) {
                replaced_twists.add(this.word.get(k));
                k++;
            } else {
                k += 6;
            }
        }

        this.word = new ArrayList<>(replaced_twists);
        replaced_twists = new ArrayList<>();

        for (DehnTwist d : this.word) {
            //TODO: Make it so we aren't using defaultEarCount

            // if this is a y
            if (d.getidentifier().getFirst() == 1 && d.getidentifier().getSecond() == Rabbit.defaultEarCount + 1) {
                //int index = word.indexOf(d);
                //word.remove(d);

                List<DehnTwist> y_twists = new ArrayList<>();

                for (int i = 3; i <= Rabbit.defaultEarCount + 1; i++) {
                    for (int j = 2; j < i; j++) {
                        y_twists.add(new DehnTwist(j, i, -1));
                    }
                }
                for (int i = 2; i <= Rabbit.defaultEarCount; i++) {
                    y_twists.add(new DehnTwist(1, i, -1));
                }

                MappingClass y_mc = new MappingClass(y_twists);
                if (d.getExp() < 0) {
                    y_mc = y_mc.inverse();
                }

                MappingClass y_mc_copy = new MappingClass(y_mc.getWord());

                for (int i = 1; i < Math.abs(d.getExp()); i++) {
                    y_mc = y_mc.multi(y_mc_copy);
                }

                replaced_twists.addAll(y_mc.getWord());

            } else {
                replaced_twists.add(d);
            }
        }

        this.word = new ArrayList<>(replaced_twists);

        // separate out the odd powers into odd powers and even powers
        // TODO: implement this
    }

    /** Slice out the 6 letters starting from the index i and checks to see if it's the identity */
    private boolean identityChecker(int i) {
        if (this.getWord().size() < i + 6) {
            return false;
        } else {
            List<DehnTwist> curr = this.getWord().subList(i, i + 6);
            return Rabbit.identityMap.containsKey(curr);
        }
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
        return (this.coset == 1);
    }

    public MappingClass parser() {
        List<DehnTwist> result_parsed = new ArrayList<>();
        for (DehnTwist d : this.getWord()) {
            if (!d.isLiftable()) {
                result_parsed.addAll(d.decomp());
            } else {
                result_parsed.add(d);
            }
        }

        this.word = result_parsed;
        return new MappingClass(result_parsed);

    }

    /**Assigns the coset of the mapping class**/
    private void findCoset() {
        HashMap<Integer, Integer> cosets = new HashMap<>();
        for (DehnTwist d : word) {
            if (!d.isLiftable()) {
                int coset_name = d.getidentifier().getFirst() - 1;
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
        MappingClass temp = new MappingClass(this.getWord());
        while (!temp.isSimplified()) {
            List<DehnTwist> newList = new ArrayList<>();
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
        // identity check
        if (this.getWord().isEmpty()) {
            return true;
        }
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

        if (this.word.get(this.word.size() - 1).getExp() == 0) {
            return false;
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

    public static MappingClass multiAll(List<MappingClass> mc_list) {
        if (mc_list.isEmpty()) {
            System.out.println("This is the identity");
            return new MappingClass(new ArrayList<DehnTwist>());
        }
        MappingClass final_mc = mc_list.get(0);
        for (int i = 1; i < mc_list.size(); i++) {
            final_mc = final_mc.multi(mc_list.get(i));
        }
        return final_mc;
    }

    /** Go CS **/
    public MappingClass conjugate(MappingClass conjugator) {
        if (this.commutesWith(conjugator)) {
            return this;
        }
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

    public void append( int i, DehnTwist d) {
        this.word.add(i, d);
        preprocessing();
        findCoset();
    }

    // overloaded without index
    public void append(DehnTwist d) {
        this.word.add(d);
        preprocessing();
        findCoset();
    }

    /**This function is ONLY VALID for the THREE-EARED CASE**/
    public List<MappingClass> smartAdd(List<MappingClass> list_mc, MappingClass m) {
        if (m.getWord().size() == 1) {
            DehnTwist d = m.getWord().get(0);
            DehnTwist base = new DehnTwist(d.getidentifier(), 1);
            int e = Math.abs(d.getExp());
            int sign = Math.round(Math.signum(d.getExp()));
            if (base.isLiftable()) {
                for (int i = 0; i < e; i++) {
                    list_mc.add(new MappingClass(new DehnTwist(d.getidentifier(), sign)));
                }
            } else {
                for (int i = 0; i < e/2; i++) {
                    list_mc.add(new MappingClass(new DehnTwist(m.getWord().get(0).getidentifier(), 2*sign)));
                }
                if (e % 2 != 0) {
                    list_mc.add(new MappingClass(new DehnTwist(m.getWord().get(0).getidentifier(), sign)));
                }
            }
        } else if (m.getWord().size() == 3) {
            DehnTwist d = m.getWord().get(1);
            DehnTwist base = new DehnTwist(d.getidentifier(), 1);
            int e = Math.abs(m.getWord().get(1).getExp());
            int sign = Math.round(Math.signum(m.getWord().get(1).getExp()));
            if (base.isLiftable()) {
                for (int i = 0; i < e; i++) {
                    List<DehnTwist> new_mc = new ArrayList<>();
                    new_mc.add(m.getWord().get(0));
                    new_mc.add(new DehnTwist(m.getWord().get(1).getidentifier(), sign));
                    new_mc.add(m.getWord().get(2));
                    list_mc.add(new MappingClass(new_mc));
                }
            } else {
                for (int i = 0; i < e/2; i++) {
                    List<DehnTwist> new_mc = new ArrayList<>();
                    new_mc.add(m.getWord().get(0));
                    new_mc.add(new DehnTwist(m.getWord().get(1).getidentifier(), 2*sign));
                    new_mc.add(m.getWord().get(2));
                    list_mc.add(new MappingClass(new_mc));
                }
                if (e % 2 != 0) {
                    List<DehnTwist> new_mc = new ArrayList<>();
                    new_mc.add(m.getWord().get(0));
                    new_mc.add(new DehnTwist(m.getWord().get(1).getidentifier(), sign));
                    new_mc.add(m.getWord().get(2));
                    list_mc.add(new MappingClass(new_mc));
                }
            }
        } else if (m.getWord().size() == 5) {
            DehnTwist d = m.getWord().get(2);
            DehnTwist base = new DehnTwist(d.getidentifier(), 1);
            int e = Math.abs(m.getWord().get(2).getExp());
            int sign = Math.round(Math.signum(m.getWord().get(2).getExp()));
            if (base.isLiftable()) {
                for (int i = 0; i < e; i++) {
                    List<DehnTwist> new_mc = new ArrayList<>();
                    new_mc.add(m.getWord().get(0));
                    new_mc.add(m.getWord().get(1));
                    new_mc.add(new DehnTwist(m.getWord().get(2).getidentifier(), sign));
                    new_mc.add(m.getWord().get(3));
                    new_mc.add(m.getWord().get(4));
                    list_mc.add(new MappingClass(new_mc));
                }
            } else {
                for (int i = 0; i < e/2; i++) {
                    List<DehnTwist> new_mc = new ArrayList<>();
                    new_mc.add(m.getWord().get(0));
                    new_mc.add(m.getWord().get(1));
                    new_mc.add(new DehnTwist(m.getWord().get(2).getidentifier(), 2*sign));
                    new_mc.add(m.getWord().get(3));
                    new_mc.add(m.getWord().get(4));
                    list_mc.add(new MappingClass(new_mc));
                }
                if (e % 2 != 0) {
                    List<DehnTwist> new_mc = new ArrayList<>();
                    new_mc.add(m.getWord().get(0));
                    new_mc.add(m.getWord().get(1));
                    new_mc.add(new DehnTwist(m.getWord().get(2).getidentifier(), sign));
                    new_mc.add(m.getWord().get(3));
                    new_mc.add(m.getWord().get(4));
                    list_mc.add(new MappingClass(new_mc));
                }
            }
        } else {
            throw new InvalidParameterException("Input is not a conjugate of the form we want");
        }

        return list_mc;
    }

    /** Computes one of the four projections to F2 possible for a 3-eared Mapping Class **/
    public MappingClass projToF2(int point) {
        List<DehnTwist> newTwist = new ArrayList<>();
        DehnTwist x = new DehnTwist(3, 4, 1);
        DehnTwist c = new DehnTwist(2, 4, 1);
        DehnTwist z = new DehnTwist(2, 1, 1);
        DehnTwist b = new DehnTwist(1, 3, 1);
        DehnTwist w = new DehnTwist(2, 3, 1);
        DehnTwist y = new DehnTwist(1, 4, 1);
        if (point == 1) {
            for (DehnTwist d : this.getWord()) {
                if (!d.equals(z) && !d.equals(b) && !d.equals(y) && !d.equals(z.inverse()) && !d.equals(b.inverse()) && !d.equals(y.inverse())) {
                    newTwist.add(d);
                }
            }
        } else if (point == 2) {
            for (DehnTwist d : this.getWord()) {
                if (!d.equals(z) && !d.equals(c) && !d.equals(w) && !d.equals(z.inverse()) && !d.equals(c.inverse()) && !d.equals(w.inverse())) {
                    newTwist.add(d);
                }
            }
        } else if (point == 3) {
            for (DehnTwist d : this.getWord()) {
                if (!d.equals(w) && !d.equals(b) && !d.equals(x) && !d.equals(w.inverse()) && !d.equals(b.inverse()) && !d.equals(x.inverse())) {
                    newTwist.add(d);
                }
            }
        } else if (point == 4) {
            for (DehnTwist d : this.getWord()) {
                if (!d.equals(x) && !d.equals(c) && !d.equals(y) && !d.equals(x.inverse()) && !d.equals(c.inverse()) && !d.equals(y.inverse())) {
                    newTwist.add(d);
                }
            }
        }

        return new MappingClass(newTwist).simplify();
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
        // TODO: this is currently specialized for 3 ears
        List<MappingClass> nonliftableGenerator = nonliftableFactor.nonliftableFactor3();


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
            MappingClass conjugating_elem = new MappingClass(conjugatingTwists);

//            if (!conjugating_elem.nonliftableValid()) {
//                System.out.println(conjugating_elem);
//            }

            // if the conjugating element is not liftableValid (has higher powers)
//            while (!conjugating_elem.nonliftableValid()) {
//                System.out.println("I am printing out an invalid connjugator before I try coset helper on it");
//                System.out.println(conjugating_elem);
//                // fix the conjugating element to become
////                List<MappingClass> conj_generators = conjugating_elem.comb();
//                result.addAll(conjugating_elem.cosetHelper(liftableElem));
//            }

            // TODO: add a condition here that if this is already just a conjugate then we don't do anything
            if (conjugating_elem.getWord().size() == 1) {
                DehnTwist leading = conjugating_elem.getWord().get(0);
                if (leading.getExp() == -1) {
                    // first add a -2 power of the conjugating elt
                    DehnTwist conj_front = new DehnTwist(leading.getidentifier(), -2);
                    // add a power 2 at the end
                    DehnTwist conj_back = new DehnTwist(leading.getidentifier(), 2);
                    result.add(new MappingClass(conj_front));
                    // conjugate by the inverse
                    result = smartAdd(result, liftableElem.conjugate(conjugating_elem.inverse()));
                    result.add(new MappingClass(conj_back));
                } else {
                    result = smartAdd(result, MC);
                }
            } else if (conjugating_elem.isLiftable()) {
                // process the conjugating element through the nonliftable factor function
                // add in the generators for the conjugating element
                // TODO: this is currently specialized for 3 ears
                List<MappingClass> conj_generators = conjugating_elem.nonliftableFactor3();
                result.addAll(conj_generators);
                // add the conjugated liftable term
                result = smartAdd(result, liftableElem);
                // add the inverse of the conjugating element
                result.addAll(invertAll(conj_generators));
            } else {
                result.addAll(conjugating_elem.cosetHelper(liftableElem));
            }
        }

        result.addAll(nonliftableGenerator);
        return result;
    }

    /** This function should parse through an originally nonliftable element with purely nonliftable letters and lifts
     *  it via cosets **/
    private List<MappingClass> cosetHelper(MappingClass liftableElem) {

        List<MappingClass> result = new ArrayList<>();
        List<MappingClass> conj_generators = new ArrayList<>();

        DehnTwist c = new DehnTwist(2, 4, 1);
        DehnTwist x = new DehnTwist(3, 4, 1);
        MappingClass c_mc = new MappingClass(c);
        MappingClass x_mc = new MappingClass(x);

        //The c coset
        if (this.getCoset() == 2) {
            this.append(c.inverse());
            this.simplify();
            if (this.getWord().get(this.getWord().size() - 1).getExp() == -2) {

                this.getWord().remove(this.getWord().size() - 1);
                conj_generators = this.nonliftableFactor3();

                conj_generators.add(c_mc.multi(c_mc).inverse());

                result.addAll(conj_generators);

                // now we conjugate the liftable thing by c
                result = smartAdd(result, liftableElem.conjugate(c_mc));

                // then add in all the inverses
                result.addAll(invertAll(conj_generators));

            } else {
                conj_generators = this.nonliftableFactor3();

                result.addAll(conj_generators);

                // now we conjugate the liftable thing by c
                result = smartAdd(result, liftableElem.conjugate(c_mc));

                // then add in all the inverses
                result.addAll(invertAll(conj_generators));
            }

        //The x coset
        } else if (this.getCoset() == 3) {
            this.append(x.inverse());
            this.simplify();
            if (this.getWord().get(this.getWord().size() - 1).getExp() == -2) {

                this.getWord().remove(this.getWord().size() - 1);
                conj_generators = this.nonliftableFactor3();

                conj_generators.add(x_mc.multi(x_mc).inverse());

                result.addAll(conj_generators);

                // now we conjugate the liftable thing by x
                result = smartAdd(result, liftableElem.conjugate(x_mc));

                // then add in all the inverses
                result.addAll(invertAll(conj_generators));

            } else {
                conj_generators = this.nonliftableFactor3();

                result.addAll(conj_generators);

                // now we conjugate the liftable thing by x
                result = smartAdd(result, liftableElem.conjugate(x_mc));

                // then add in all the inverses
                result.addAll(invertAll(conj_generators));
            }

        //The cx coset
        } else if (this.getCoset() == 6) {
            this.append(x.inverse());
            this.append(c.inverse());
            this.simplify();

            // This is for if we had a cx for this and adding+simplifying makes the list empty. This avoids index -1 error
            if (this.getWord().isEmpty()) {
                return new ArrayList<>();
            }
            //In this case the word must have a c^-2 at the end
            if (this.getWord().get(this.getWord().size() - 1).getExp() == -2) {
                this.getWord().remove(this.getWord().size() - 1);

                conj_generators = this.nonliftableFactor3();

                conj_generators.add(c_mc.multi(c_mc).inverse());

                result.addAll(conj_generators);

                // now we conjugate the liftable thing by cx
                result = smartAdd(result, liftableElem.conjugate(c_mc.multi(x_mc)));

                // then add in all the inverses
                result.addAll(invertAll(conj_generators));

            //For this case the word will have a x^-2c^-1 at the end
            } else if (this.getWord().get(this.getWord().size() - 2).getExp() == -2) {
                this.getWord().remove(this.getWord().size() - 1);
                this.getWord().remove(this.getWord().size() - 1);

                //In this case, conjugation will create a c^{-2} at the end
                if (this.getWord().get(this.getWord().size() - 1).getExp() == -1) {
                    this.getWord().remove(this.getWord().size() - 1);

                    conj_generators = this.nonliftableFactor3();

                    conj_generators.add(c_mc.multi(c_mc).inverse());
                    conj_generators.add(x_mc.multi(x_mc).inverse().conjugate(c_mc));

                    result.addAll(conj_generators);

                    // now we conjugate the liftable thing by cx
                    result = smartAdd(result, liftableElem.conjugate(c_mc.multi(x_mc)));

                    // then add in all the inverses
                    result.addAll(invertAll(conj_generators));

                //In the other case, a c will be removed from the end
                } else {
                    this.getWord().remove(this.getWord().size() - 1);

                    conj_generators = this.nonliftableFactor3();

                    conj_generators.add(x_mc.multi(x_mc).inverse().conjugate(c_mc));

                    result.addAll(conj_generators);

                    // now we conjugate the liftable thing by cx
                    result = smartAdd(result, liftableElem.conjugate(c_mc.multi(x_mc)));

                    // then add in all the inverses
                    result.addAll(invertAll(conj_generators));
                }

            }

        } else {
            throw new InvalidParameterException("Ma'am this is a Wendy's - this element is not in a correct coset");
        }

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
                // TODO: resolve this dangerous op
                this.simplify();
                this.parser();
                factoredMC.addAll(this.liftableFactor());
                return factoredMC;
            } else {
                // if the current thing is not liftable, we are simply building up the conjugator
                conjugator.add(this.getWord().get(i));
            }
        }

        this.simplify();

        // wrong powers in the nonliftable factor
        if (!this.nonliftableValid()) {
            System.out.println("printing out the invalid factor");
            System.out.println(this);
            while (!this.nonliftableValid()) {
                this.simplify();
                factoredMC.addAll(this.liftableFactor());
            }
            return factoredMC;
        }


        factoredMC.add(this);
        return factoredMC;
    }

    public List<MappingClass> nonliftableFactor3() {
        // simplify and check for invalid inputs
        this.simplify();

        if (this.getWord().isEmpty()) {
            return new ArrayList<>();
        }

        if (!this.nonliftableValid()) {
            System.out.println("This is the element you are trying to regard as a nonliftable factor:");
            System.out.println(this);
            throw new InvalidParameterException("the portion with pure nonliftable factors is impure");
        }

        List<MappingClass> result = new ArrayList<>();

        DehnTwist first_elt = this.getWord().remove(0);
        // if the element itself is liftable
        if (this.getWord().size() == 0) {
            result.add(new MappingClass(first_elt));
            return result;
        }
        if (first_elt.getExp() == 2 || first_elt.getExp() == -2) {
            // if the first twist is a square, separate it out into the results
            result.add(new MappingClass(first_elt));
            // call the function recursively
            result.addAll(this.nonliftableFactor3());
            return result;
        }

        DehnTwist second_elt = this.getWord().remove(0);
        DehnTwist third_elt = this.getWord().remove(0);
        boolean flag = false;

        if (first_elt.getExp() < 0) {
            result.add(new MappingClass(new DehnTwist(first_elt.getidentifier(), -2)));
            first_elt = first_elt.inverse();
            // this.append(0, first_elt.inverse());
        }

        if (second_elt.getExp() < 0) {
            DehnTwist second_sqr = new DehnTwist(second_elt.getidentifier(), -2);
            result.add(new MappingClass(second_sqr).conjugate(new MappingClass(first_elt)));
            second_elt = second_elt.inverse();
            flag = true;
        }

        if (third_elt.getExp() == - first_elt.getExp()) {
            // in this case, we make a commutator out of the first two things
            MappingClass first = new MappingClass(first_elt);
            MappingClass second = new MappingClass(second_elt);
            result.add(first.commutator(second));

            // and append a copy of the second elt
            this.append(0, second_elt);
        } else if (third_elt.getExp() == first_elt.getExp()) {
            // in this case, we first extract a commutator as usual
            MappingClass first = new MappingClass(first_elt);
            MappingClass second = new MappingClass(second_elt);
            result.add(first.commutator(second));

            // then we extract a conjugate of the square of the first element by the second element
            DehnTwist first_squared_twist = new DehnTwist(first_elt.getidentifier(), first_elt.getExp() * 2);
            MappingClass first_squared = new MappingClass(first_squared_twist);
            result.add(first_squared.conjugate(second));

            // then we append a copy of the second elt
            this.append(0, second_elt);
        }

        // if we did the xc^-1x^-1c shenanigans we must add in a new c^2
        if (flag) {
            result.add(new MappingClass(new DehnTwist(second_elt.getidentifier(), 2)));
        }

        // call the function recursively
        result.addAll(this.nonliftableFactor3());
        return result;
    }

    private boolean nonliftableValid() {
        for (int i = 1; i < this.getWord().size(); i++) {
            DehnTwist t = this.getWord().get(i);
            DehnTwist newT = new DehnTwist(this.getWord().get(i).getidentifier(), 1);
            if (newT.isLiftable() || t.getExp() > 1 || t.getExp() < -1) {
                return false;
            }
        }
        return true;
    }

    public List<DehnTwist> getWord() {
        return this.word;
    }

    public int getCoset() {
        return this.coset;
    }

    public int getLength() {
        int length = 0;
        for (DehnTwist twist : this.getWord()) {
            length += Math.abs(twist.getExp());
        }
        this.length = length;
        return length;
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
    public int hashCode() {
        return this.toString().hashCode();
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
