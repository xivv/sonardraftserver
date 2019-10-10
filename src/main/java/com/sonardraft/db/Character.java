package com.sonardraft.db;

import org.opencv.core.Mat;

public class Character {

	private String name;
	private Mat mat;

	public Character(Mat mat, String name) {
		super();
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

}
