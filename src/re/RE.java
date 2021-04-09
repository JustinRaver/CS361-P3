package re;

import fa.State;
import fa.nfa.NFA;
import fa.nfa.NFAState;

/**
 * @author JustinRaver, NickStolarow
 * @version 1.0
 */
public class RE implements REInterface {
    //instance variables
    private String regex;
    private int stateCount;
    private int comboCount;


    /**
     * Basic Constructor
     * @param regex a user defined regex
     */
    public RE(String regex) {
        this.regex = regex;
        this.stateCount = 1;
        this.comboCount = 1;
    }

    @Override
    public NFA getNFA() {
        //Take the first term from the nfa
        NFA nfaTerm = getNfaTerm();
        //check for the or
        if (notParsed() && peek() == '|') {
            consume('|');
            NFA nfa = getNFA();
            return combination(nfaTerm, nfa);
        }
        return nfaTerm;
    }

    /**
     * Combines two existing Nfas into one
     * @param main First NFA created
     * @param secondary Secon NFA created
     * @return The combined NFA
     */
    public NFA combination(NFA main, NFA secondary) {
        //Create a new nfa
        NFA ret = new NFA();
        //add a start state to the nfa
        ret.addStartState("cs" + comboCount);
        comboCount++;
        //add the states and alphabets from the previous NFA's
        ret.addNFAStates(main.getStates());
        ret.addAbc(main.getABC());
        ret.addNFAStates(secondary.getStates());
        ret.addAbc(secondary.getABC());

        //Now add empty transitions from the new nfas start to the two nfas starting states
        ret.addTransition(ret.getStartState().toString(), 'e', main.getStartState().toString());
        ret.addTransition(ret.getStartState().toString(), 'e', secondary.getStartState().toString());

        System.out.println("COMBINATION:");
        System.out.println(ret);
        return ret;
    }

    /**
     * Gets the first term from a regex and creates a representative NFA
     * @return The Nfa for the term in the regex
     */
    public NFA getNfaTerm() {
        //Create a new NFA
        NFA nfaFactor = new NFA();
        //while the regex isn't parsed and were not parsing a special symbol
        while (notParsed() && peek() != ')' && peek() != '|') {
            //get the regex factor NFA
            NFA nextNfaFactor = getNfaFactor();
            //combine the two nfas in a sequence
            nfaFactor = sequence(nfaFactor, nextNfaFactor);
        }
        return nfaFactor;
    }

    /**
     * This method takes in two NFA's and returns the combined sequential NFA
     * @param main the first NFA created
     * @param secondary the second NFA created
     * @return the two NFAs connected with an empty transition
     */
    public NFA sequence(NFA main, NFA secondary) {
        //Check if main has any states
        if (main.getStates().isEmpty()) {
            //if not then return secondary
            return secondary;
        }

        //Add the states and alphabet from second to main
        main.addNFAStates(secondary.getStates());
        main.addAbc(secondary.getABC());

        //Iterate through the final states
        for (State s : main.getFinalStates()) {
                //remove final states from main
                if(!secondary.getFinalStates().contains(s)){
                    //connect the final states of main to the start state of second via empty transition
                    main.addTransition(s.toString(), 'e', secondary.getStartState().toString());

                    //cast s to an NFAState to use set Non final method
                    NFAState state = (NFAState) s;
                    state.setNonFinal();
                }
        }
        System.out.println("SEQUENCE:");
        System.out.println(main);

        return main;
    }

    /**
     * Get the next factor within the regex
     * @return the resulting NFA
     */
    public NFA getNfaFactor() {
        //NFA produced by regex
        NFA baseNfa = getNfaBase();
        //looking for repetitions
        while (notParsed() && peek() == '*') {
            consume('*');
            //create a repetition
            baseNfa = repetition(baseNfa);
        }
        return baseNfa;
    }

    /**
     * Alters an NFA state to represent 0 or more of something in a regex
     * @param main an nfa that should repeat
     * @return nfa with the repetition added
     */
    public NFA repetition(NFA main) {
        //for each final state in the NFA
        for (State s : main.getFinalStates()) {
            //add an empty transition to the starting state
            main.addTransition(s.toString(), 'e', main.getStartState().toString());
            //add an empty transition from the starting state to the final state
            main.addTransition(main.getStartState().toString(), 'e', s.toString());
        }

        System.out.println("REPETITION:");
        System.out.println(main);
        return main;
    }

    /**
     * Creates an NFA or recursively calls getNFA if there are parentheses
     * @return an nfa representing the current character
     */
    public NFA getNfaBase() {
        //if there is a '(' then recursively call getNFA
        if (peek() == '(') {
            consume('(');
            NFA nfa = getNFA();
            consume(')');
            return nfa;
        }
        //Creates an new NFA
        NFA nfa = new NFA();
        //Adds a start state
        nfa.addStartState("s" + stateCount);
        //Adds a final state
        nfa.addFinalState("f" + stateCount);
        //Adds transition from start to final on the regex character
        nfa.addTransition("s" + stateCount, next(), "f" + stateCount);

        //increment the count
        stateCount++;

        System.out.println("BASE:");
        System.out.println(nfa);
        return nfa;
    }

    /**
     * Returns the next character in the string without altering it
     * @return
     */
    private char peek() {
        return regex.charAt(0);
    }

    /**
     * Removes the next character in the string
     * @param c the next character in the string
     */
    private void consume(char c) {
        if (peek() == c) {
            this.regex = this.regex.substring(1);
        } else {
            throw new RuntimeException("Expected: " + c + " Got: " + peek());
        }
    }

    /**
     * Returns and removes the next character in the string
     * @return character from regex
     */
    private char next() {
        char c = peek();
        consume(c);
        return c;
    }

    /**
     *
     * @return true if the strings length is greater than 0 false otherwise
     */
    private boolean notParsed() {
        return regex.length() > 0;
    }


}
