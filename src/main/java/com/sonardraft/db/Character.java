package com.sonardraft.db;

import org.opencv.core.Mat;

public class Character {

	private String name;
	private Mat mat;
	private Mat histogramm;
	private Integer priority = 0;

	public Integer getPriority(Draft draft) {

		// Draft logic

		if (name == "Yasou") {

			// Pick
			// Orianna, Malphite, Jarvan

			// Bann
			// Pantheon

		}

		return priority;
	}

	public Character(String name) {
		super();
		this.name = name;
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

}
