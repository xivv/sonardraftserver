package com.sonardraft.db;

import java.util.ArrayList;
import java.util.List;

import com.sonardraft.db.enums.Role;

public class Team {

	private List<Character> picks;
	private List<Character> banns;
	private List<Character> combos;
	private List<Role> openRoles;

	public List<Role> getOpenRoles() {
		return openRoles;
	}

	public void setOpenRoles(List<Role> openRoles) {
		this.openRoles = openRoles;
	}

	public Team() {
		picks = new ArrayList<>();
		banns = new ArrayList<>();
		setCombos(new ArrayList<>());
		setOpenRoles(new ArrayList<>());
	}

	public List<Character> getPicks() {
		return picks;
	}

	public void setPicks(List<Character> picks) {
		this.picks = picks;
	}

	public List<Character> getBanns() {
		return banns;
	}

	public void setBanns(List<Character> banns) {
		this.banns = banns;
	}

	public List<Character> getCombos() {
		return combos;
	}

	public void setCombos(List<Character> combos) {
		this.combos = combos;
	}

}
