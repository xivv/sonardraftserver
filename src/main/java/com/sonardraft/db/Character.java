package com.sonardraft.db;

import com.sonardraft.db.enums.Feature;
import com.sonardraft.db.enums.Role;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class Character {

	private Mat mat;
	private Mat histogramm;

	private String name;
	private List<Role> roles = new ArrayList<>();

	private List<Character> combos  = new ArrayList<>();
	private List<Character> counter = new ArrayList<>();

	private List<Feature> features = new ArrayList<>();

	private Integer priorityBonus = 0;
	private Integer priority = 0;

	public Character() {

	}

	public Character(String name) {
		super();
		this.name = name;
	}

	public Character(String name, Integer priority) {
		super();
		this.name = name;
		this.priority = priority;
	}

	public Character(Mat mat, String name) {
		super();
		this.name = name;
		this.mat = mat;
	}

	public Character(Mat mat, Mat histogramm, String name) {
		super();
		this.histogramm = histogramm;
		this.name = name;
		this.mat = mat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Mat getMat() {
		return mat;
	}

	public void setMat(Mat mat) {
		this.mat = mat;
	}

	public Mat getHistogramm() {
		return histogramm;
	}

	public void setHistogramm(Mat histogramm) {
		this.histogramm = histogramm;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public List<Character> getCombos () {
		return combos;
	}

	public void setCombos ( List<Character> combos ) {
		this.combos = combos;
	}

	public Integer getPriorityBonus() {
		return priorityBonus;
	}

	public void setPriorityBonus(Integer priorityBonus) {
		this.priorityBonus = priorityBonus;
	}

	public Character(Character character) {

		this.setPriorityBonus(character.priorityBonus);
		this.setName(character.name);
		this.setRoles(character.roles);
	}

	public Integer getPriority() {
		return this.priority;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	public List<Character> getCounter () {
		return counter;
	}

	public void setCounter ( List<Character> counter ) {
		this.counter = counter;
	}
}
