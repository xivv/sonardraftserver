package com.sonardraft.db;

import java.util.ArrayList;
import java.util.List;

public class Comp {

    private String          name;
    private List<CompCharacter> picks = new ArrayList<> ();
    private List<Character>    banns = new ArrayList<> ();

    public List<CompCharacter> getPicks () {
        return picks;
    }

    public void setPicks ( List<CompCharacter> picks ) {
        this.picks = picks;
    }

    public List<Character> getBanns () {
        return banns;
    }

    public void setBanns ( List<Character> banns ) {
        this.banns = banns;
    }

    public String getName () {
        return name;
    }

    public void setName ( String name ) {
        this.name = name;
    }
}
