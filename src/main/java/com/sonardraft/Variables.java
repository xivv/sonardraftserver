package com.sonardraft;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.opencv.imgproc.Imgproc;

public class Variables {

	/**
	 * Konfiguration
	 */
	public static final String CHARACTERURL = "https://na.leagueoflegends.com/en/game-info/champions/";
	public static final String[] IMAGEFORMATS = { "png" };

	public static final String BASE = "C:\\Users\\Sawe\\git\\leagueocr\\";
	public static final String CHARACTERPATH = BASE + "characters";
	public static final String RESULTPATH = BASE + "result\\";
	public static final String SCREENPATH = BASE + "screenshots\\";

	public static final int METHOD = Imgproc.TM_CCOEFF;
	public static Screensize resolution;

	/*
	 * Performance
	 */
	public static final int SCREENSHOTINTERVALL = 500;

	/*
	 */
	protected static final int[] HISTOGRAMMSIZE = { 64, 64, 64 };
	protected static final float[] HISTOGRAMMRANGE = { 0, 128, 0, 128, 0, 128 };
	protected static final int[] HISTOGRAMMCHANNELS = { 0, 1, 2 };

	public static void init() {

		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

		if (screensize.width == 1920 && screensize.height == 1080) {
			resolution = Screensize.x1920x1080;
		} else if (screensize.width == 1024 && screensize.height == 768) {
			resolution = Screensize.x1024x768;
		}

	}

}
