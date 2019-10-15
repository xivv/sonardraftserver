package com.sonardraft.db;

import java.util.ArrayList;
import java.util.List;

public class Team {

	private List<Character> picks;
	private List<Character> banns;
	private List<Character> combos;

	public Team() {
		picks = new ArrayList<>();
		banns = new ArrayList<>();
		setCombos(new ArrayList<>());
	}

	@Override
	public String toString() {

		String result = "Team picks" + "\n";

		for (Character character : picks) {
			result += character.getName() + "\n";
		}

		return result;
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
