package com.sonardraft.db;

import com.sonardraft.db.enums.Role;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private List<Character> picks     = new ArrayList<> ();
    private List<Character> banns     = new ArrayList<> ();
    private List<Character> combos    = new ArrayList<> ();
    private List<Role>      openRoles = new ArrayList<> ();
    private List<Comp>      comps     = new ArrayList<> ();

    public List<Role> getOpenRoles () {
        return openRoles;
    }

    public void setOpenRoles ( List<Role> openRoles ) {
        this.openRoles = openRoles;
    }

    public Team () {
    }

    public List<Character> getPicks () {
        return picks;
    }

    public void setPicks ( List<Character> picks ) {
        this.picks = picks;
    }

    public List<Character> getBanns () {
        return banns;
    }

    public void setBanns ( List<Character> banns ) {
        this.banns = banns;
    }

    public List<Character> getCombos () {
        return combos;
    }

    public void setCombos ( List<Character> combos ) {
        this.combos = combos;
    }

    public List<Comp> getComps () {
        return comps;
    }

    public void setComps ( List<Comp> comps ) {
        this.comps = comps;
    }
}
