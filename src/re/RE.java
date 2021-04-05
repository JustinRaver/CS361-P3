package re;

import fa.nfa.NFA;

public class RE implements REInterface{
    private final String regex;
    public RE(String regex){
        this.regex = regex;
    }
    @Override
    public NFA getNFA() {
        return null;
    }
}
