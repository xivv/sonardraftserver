package com.sonardraft;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.google.common.io.Resources;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sonardraft.db.Character;

public class Variables {

	/**
	 * Konfiguration
	 */
	public static final String CHARACTERURL = "https://na.leagueoflegends.com/en/game-info/champions/";
	public static final String[] IMAGEFORMATS = { "png" };

	public static String BASE = "";
	public static String CHARACTERPATH = BASE + "characters\\";
	public static String RESULTPATH = BASE + "result\\";
	public static String SCREENPATH = BASE + "screenshots\\";

	public static final int METHOD = Imgproc.TM_SQDIFF_NORMED;
	public static Screensize resolution;

	/*
	 * Quality
	 */
	protected static final int[] HISTOGRAMMSIZE = { 64, 64, 64 };
	protected static final float[] HISTOGRAMMRANGE = { 0, 128, 0, 128, 0, 128 };
	protected static final int[] HISTOGRAMMCHANNELS = { 0, 1, 2 };

	/*
	 * Statics
	 */
	public static List<Character> characters = new ArrayList<>();

	private Variables() {
	}

	public static void createFreshConfiguration() throws IOException {

		BASE = System.getProperty("user.dir") + "\\";
		for (File character : new File(Variables.CHARACTERPATH).listFiles()) {

			if (!character.isDirectory()) {
				Variables.characters.add(new Character(FilenameUtils.getBaseName(character.getName())));
			}
		}

		String freshConfiguration = new Gson().toJson(characters);
		try (FileOutputStream outputStream = new FileOutputStream(BASE + "priorities.original.json")) {
			byte[] strToBytes = freshConfiguration.getBytes();
			outputStream.write(strToBytes);
		}
	}

	public static void initialiseCharacters() throws IOException {

		// We resize the images to match our resolution
		Tools.resizeImages(Variables.CHARACTERPATH, 64);

		// We iterate trough all configured characters
		File characterFolder = new File(Resources.getResource("characters").getPath());

		for (File file : characterFolder.listFiles()) {

			if (FilenameUtils.getExtension(file.getName()).equals("json")) {

				// Load the character configuration
				String priorityProperties = Resources.toString(file.toURI().toURL(), StandardCharsets.UTF_8);
				Character character = new Gson().fromJson(priorityProperties, new TypeToken<Character>() {
				}.getType());

				// Load the characters image/mat

				Mat mat = Imgcodecs
						.imread(Variables.CHARACTERPATH + FilenameUtils.getBaseName(file.getName()) + ".png");
				character.setMat(mat);
				Variables.characters.add(character);
			}
		}

	}

	public static boolean init() {

		BASE = System.getProperty("user.dir") + "\\";

//		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
//
//		if (screensize.width == 1920 && screensize.height == 1080) {
//			resolution = Screensize.x1920x1080;
//		} else if (screensize.width == 1024 && screensize.height == 768) {
//			resolution = Screensize.x1024x768;
//		}

		// Get configured character combo properties
		try {
			initialiseCharacters();
		} catch (IOException e) {
			System.out.println("Couldnt initialise characters");
			e.printStackTrace();
		}

		return true;
	}

}
