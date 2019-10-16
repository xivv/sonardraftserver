package com.sonardraft.db;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

public class Character {

	private Mat mat;
	private Mat histogramm;

	private String name;
	private List<Role> roles = new ArrayList<>();

	private List<Character> priorities = new ArrayList<>();

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

	public List<Character> getPriorities() {
		return priorities;
	}

	public void setPriorities(List<Character> priorities) {
		this.priorities = priorities;
	}

	public Integer getPriorityBonus() {
		return priorityBonus;
	}

	public void setPriorityBonus(Integer priorityBonus) {
		this.priorityBonus = priorityBonus;
	}

	public Character clone() {

		Character newCharacter = new Character();

		newCharacter.setPriorityBonus(this.priorityBonus);
		newCharacter.setName(this.name);
		newCharacter.setRoles(this.roles);

		for (Role role : this.roles) {

		}

		return newCharacter;
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

}
