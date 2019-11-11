package com.sonardraft.db;

import java.util.ArrayList;
import java.util.List;

public class Draft {

    private List<Comp> comps = new ArrayList<> ();
    private Team       blue;
    private Team       red;

    public Draft () {
        blue = new Team ();
        red = new Team ();
    }

    public List<Comp> getComps () {
        return comps;
    }

    public void setComps ( List<Comp> comps ) {
        this.comps = comps;
    }

    public Team getBlue () {
        return blue;
    }

    public void setBlue ( Team blue ) {
        this.blue = blue;
    }

    public Team getRed () {
        return red;
    }

    public void setRed ( Team red ) {
        this.red = red;
    }

}
