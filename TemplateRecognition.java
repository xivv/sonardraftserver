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
				new Scalar(0, 255, 0), 2, 8, 0);

		return Tools.toBufferedImage(HighGui.toBufferedImage(screenshot));
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

			System.out.println(character.getName() + ": " + Core.minMaxLoc(result).minVal);
			// if (Core.minMaxLoc(result).minVal > 0) {

			Tools.saveBufferedImage(createResultImage(matchLoc, mat, character.getMat()),
					RESULTPATH + character.getName() + ".png");
			// }
		}

	}

	private static Mat screenshot() {
		return Imgcodecs.imread(BASE + "19201080.png");
	}

}
