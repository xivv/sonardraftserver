package com.sonardraft.db;

import java.util.ArrayList;
import java.util.List;

public class CompCharacter extends Character {

    List<Character> alternatives = new ArrayList<> ();

    public List<Character> getAlternatives () {
        return alternatives;
    }

    public void setAlternatives ( List<Character> alternatives ) {
        this.alternatives = alternatives;
    }
}
