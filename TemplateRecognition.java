package com.sonardraft;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.sonardraft.db.Character;

public class TemplateRecognition {

	private static List<Character> characters = new ArrayList<>();

	/**
	 * Konfiguration
	 */

	private static final String BASE = "C:\\Users\\mancuso1\\Desktop\\leagueocr\\";
	private static final String CHARACTERPATH = BASE + "characters";
	private static final String RESULTPATH = BASE + "result\\";

	private static final int METHOD = Imgproc.TM_SQDIFF;

	private TemplateRecognition() {

	}

	public static void init() {

		Tools.clearFolder(new File(RESULTPATH));
		Tools.resizeImages(CHARACTERPATH, 64);

		File file = new File(CHARACTERPATH);

		for (File character : file.listFiles()) {

			if (!character.isDirectory()) {
				characters.add(new Character(Imgcodecs.imread(character.getAbsolutePath()),
						FilenameUtils.getBaseName(character.getName())));
			}
		}
	}

	private static Mat prepareResult(Mat screenshot, Mat template) {
		Mat result = new Mat();
		int columns = screenshot.cols() - template.cols() + 1;
		int rows = screenshot.rows() - template.rows() + 1;
		result.create(rows, columns, CvType.CV_32FC1);

		return result;
	}

	private static BufferedImage createResultImage(Point matchLoc, Mat screenshot, Mat template) {

		Imgproc.rectangle(screenshot, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()),
				new Scalar(0, 255, 0), 2, 8, 0);

		return Tools.toBufferedImage(HighGui.toBufferedImage(screenshot));
	}

	private static List<String> expectedFinds = new ArrayList<>(Arrays.asList("Aatrox", "Akali", "Alistar", "Amumu",
			"Annie", "Ashe", "Azir", "Blitzcrank", "Brand", "Braum", "Caitlyn", "Chogath", "Darius", "DrMundo",
			"Draven", "Ekko", "Elise", "Evelynn", "Ezreal", "Fiddlesticks", "Galio", "Gangplank", "Garen", "Gragas",
			"Graves", "Hecarim", "Heimerdinger", "Irelia", "Ivern"));

	public static Double checkDifference(Mat template, Mat reference, Double minValue) {

		Mat hsvTest1 = template;
		Mat hsvTest2 = reference;
		Imgproc.cvtColor(template, hsvTest1, Imgproc.COLOR_BGR2HSV);
		Imgproc.cvtColor(reference, hsvTest2, Imgproc.COLOR_BGR2HSV);

		int hBins = 50, sBins = 60;
		int[] histSize = { hBins, sBins };
		// hue varies from 0 to 179, saturation from 0 to 255
		float[] ranges = { 0, 180, 0, 256 };
		// Use the 0-th and 1-st channels
		int[] channels = { 0, 1 };

		Mat histTest1 = new Mat(), histTest2 = new Mat();

		List<Mat> hsvTest1List = Arrays.asList(hsvTest1);
		Imgproc.calcHist(hsvTest1List, new MatOfInt(channels), new Mat(), histTest1, new MatOfInt(histSize),
				new MatOfFloat(ranges), false);
		Core.normalize(histTest1, histTest1, 0, 1, Core.NORM_MINMAX);
		List<Mat> hsvTest2List = Arrays.asList(hsvTest2);
		Imgproc.calcHist(hsvTest2List, new MatOfInt(channels), new Mat(), histTest2, new MatOfInt(histSize),
				new MatOfFloat(ranges), false);
		Core.normalize(histTest2, histTest2, 0, 1, Core.NORM_MINMAX);

		return Imgproc.compareHist(histTest2, histTest1, 0);
	}

	public static void check() {

		Mat screenshot = screenshot();

		for (Character character : characters) {

			Mat mat = new Mat();
			screenshot.copyTo(mat);

			Mat result = prepareResult(mat, character.getMat());
			Imgproc.matchTemplate(mat, character.getMat(), result, METHOD);

			Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
			Point matchLoc = Core.minMaxLoc(result).minLoc;

			Rect rect = new Rect((int) matchLoc.x, (int) matchLoc.y, 64, 64);
			Mat hist = screenshot.submat(rect);

			if (checkDifference(character.getMat(), hist, Core.minMaxLoc(result).minVal) > 0.2) {

				Tools.saveBufferedImage(createResultImage(matchLoc, screenshot, character.getMat()),
						RESULTPATH + character.getName() + ".png");
			}

		}

	}

	private static Mat screenshot() {
		return Imgcodecs.imread(BASE + "19201080.png");
	}

}
