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

    public NFA combination(NFA main, NFA secondary) {
        //Create a new nfa
        NFA ret = new NFA();
        //add a start state to the nfa
        ret.addStartState("cs" + comboCount);
        comboCount++;
        //add the states from the previous NFA's
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

    public NFA getNfaTerm() {
        NFA nfaFactor = new NFA();

        while (notParsed() && peek() != ')' && peek() != '|') {
            NFA nextNfaFactor = getNfaFactor();
            nfaFactor = sequence(nfaFactor, nextNfaFactor);
        }
        return nfaFactor;
    }

    public NFA sequence(NFA main, NFA secondary) {
        if (main.getStates().isEmpty()) {
            return secondary;
        }

        //Add the states and alphabet from second to main
        main.addNFAStates(secondary.getStates());
        main.addAbc(secondary.getABC());

        //connect the final states of main to the start state of second via empty transition
        for (State s : main.getFinalStates()) {
                main.addTransition(s.toString(), 'e', secondary.getStartState().toString());
                if(!secondary.getFinalStates().contains(s)){
                    NFAState state = (NFAState) s;
                    state.setNonFinal();
                }
        }


        System.out.println("SEQUENCE:");
        System.out.println(main);
        return main;
    }

    public NFA getNfaFactor() {
        NFA baseNfa = getNfaBase();

        while (notParsed() && peek() == '*') {
            consume('*');
            baseNfa = repetition(baseNfa);
        }
        return baseNfa;
    }

    public NFA repetition(NFA main) {
        String stateName = "f"+stateCount;
        stateCount++;
        //add new final state
        main.addFinalState(stateName);
        //for each final state add an empty transition to start state of the nfa to represent the possible repetition
        for (State s : main.getFinalStates()) {
            if(!s.toString().equals(stateName)) {
                main.addTransition(s.toString(), 'e', main.getStartState().toString());
                main.addTransition(s.toString(), 'e', stateName);
            }
        }
        main.addTransition(main.getStartState().toString(), 'e', stateName);

        System.out.println("REPETITION:");
        System.out.println(main);
        return main;
    }

    public NFA getNfaBase() {
        //if there is a '(' then recursively call getNFA
        if (peek() == '(') {
            consume('(');
            NFA nfa = getNFA();
            consume(')');
            return nfa;
        }

        NFA nfa = new NFA();

        nfa.addStartState("s" + stateCount);
        nfa.addFinalState("f" + stateCount);
        nfa.addTransition("s" + stateCount, next(), "f" + stateCount);

        //increment the count
        stateCount++;

        System.out.println("BASE:");
        System.out.println(nfa);
        return nfa;
    }

    private char peek() {
        return regex.charAt(0);
    }

    private void consume(char c) {
        if (peek() == c) {
            this.regex = this.regex.substring(1);
        } else {
            throw new RuntimeException("Expected: " + c + " Got: " + peek());
        }
    }

    private char next() {
        char c = peek();
        consume(c);
        return c;
    }

    private boolean notParsed() {
        return regex.length() > 0;
    }


}
