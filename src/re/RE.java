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


    public RE(String regex) {
        this.regex = regex;
    }

    @Override
    public NFA getNFA() {
        //Take the first term from the nfa
        NFA nfaTerm = getNfaTerm();
        //check for the or
        if (!parsed() && peek() == '|') {
            consume('|');
            NFA nfa = getNFA();
            return combine(nfaTerm,nfa);
        }
        return nfaTerm;
    }

    public NFA combine(NFA main, NFA secondary){
        for (Character c:secondary.getABC()) {
            State other = null;
            for(State state: main.getStates()){
                if(!state.equals(main.getStartState())){
                    other = state;
                    break;
                }
            }
            main.addTransition(main.getStartState().toString(),c,other.toString());
        }
        return main;
    }

    public NFA getNfaTerm() {
        NFA nfaFactor = null;

        while (!parsed() && peek() != ')' && peek() != '|') {
            NFA nextNfaFactor = getNfaFactor();
            nfaFactor = null; //dont know what to do here
        }

        return nfaFactor;
    }

    public NFA getNfaFactor() {
        NFA baseNfa = getNfaBase();

        while (!parsed() && peek() == '*') {
            consume('*');
            baseNfa = null; //not sure what to do here
        }
        return baseNfa;
    }

    public NFA getNfaBase(){
        if (peek() == '('){
            consume('(');
            NFA nfa = getNFA();
            consume(')');
            return nfa;
        }
        return null; //this is the case theres only a single character
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

    private boolean parsed() {
        return regex.length() <= 0;
    }


}
