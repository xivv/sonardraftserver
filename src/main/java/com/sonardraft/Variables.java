package com.sonardraft;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
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
	public static String CHARACTERPATH = BASE + "characters";
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

	public static boolean initialiseCharacters() {

		Tools.clearFolder(new File(Variables.RESULTPATH));
		Tools.resizeImages(Variables.CHARACTERPATH, 64);

		for (File character : new File(Variables.CHARACTERPATH).listFiles()) {

			if (!character.isDirectory()) {

				Mat characterMat = Imgcodecs.imread(character.getAbsolutePath());
				Variables.characters.add(new Character(characterMat, FilenameUtils.getBaseName(character.getName())));
			}
		}

		try {
			URL priorityUrl = Resources.getResource("priorities.original.json");
			String priorityProperties = Resources.toString(priorityUrl, StandardCharsets.UTF_8);
			List<Character> characterPriorities = new Gson().fromJson(priorityProperties,
					new TypeToken<List<Character>>() {
					}.getType());

			for (Character character : characterPriorities) {
				Character foundCharacter = Tools.findByName(Variables.characters, character.getName());

				if (foundCharacter != null) {
					foundCharacter.setPriorities(character.getPriorities());
					foundCharacter.setRoles(character.getRoles());
				} else {
					System.err
							.println("Character with name " + character.getName() + " doesnt exist in image directory");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
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
		initialiseCharacters();

		return true;
	}

}
