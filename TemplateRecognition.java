package com.sonardraft;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.sonardraft.db.Character;

public class TemplateRecognition {

	private static final String BASE = "C:\\Users\\Sawe\\Desktop\\leagueocr\\";
	private static final String CHARACTERPATH = "characters";
	private static final String RESULTPATH = "result\\";

	private static List<Character> characters = new ArrayList<>();

	/**
	 * Konfiguration
	 */

	private static final int METHOD = Imgproc.TM_SQDIFF_NORMED;

	private TemplateRecognition() {

	}

	public static void init() {

		Tools.clearFolder(new File(BASE + RESULTPATH));

		File file = new File(BASE + CHARACTERPATH);

		for (File character : file.listFiles()) {

			if (!character.isDirectory()) {
				characters.add(new Character(Imgcodecs.imread(character.getAbsolutePath()), character.getName()));
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
				new Scalar(255, 0, 0), 2, 8, 0);

		return Tools.toBufferedImage(HighGui.toBufferedImage(screenshot));
	}

	public static void check() {

		Mat screenshot = screenshot();

		for (Character character : characters) {
			Mat result = prepareResult(screenshot, character.getMat());
			Imgproc.matchTemplate(screenshot, character.getMat(), result, METHOD);

			Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
			Point matchLoc = Core.minMaxLoc(result).maxLoc;

			System.out.println(character.getName() + ": " + Core.minMaxLoc(result).minVal);
			if (Core.minMaxLoc(result).minVal > 0) {

				Mat mat = new Mat();
				screenshot.copyTo(mat);

				Tools.saveBufferedImage(createResultImage(matchLoc, mat, character.getMat()),
						BASE + RESULTPATH + character.getName() + ".png");
			}
		}

	}

	private static Mat screenshot() {
		return Imgcodecs.imread(BASE + "19201080pick.png");
	}

}
