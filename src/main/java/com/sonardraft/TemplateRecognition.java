package com.sonardraft;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
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

	private TemplateRecognition() {

	}

	public static Character getCharacterFromTemplateDouble(File file, boolean saveResult) {

		Mat screenshot = Imgcodecs.imread(file.getAbsolutePath());
		Character bestCharacter = null;
		Double bestValue = 0d;

		for (Character character : characters) {

			Mat mat = new Mat();
			screenshot.copyTo(mat);

			Mat result = prepareResult(mat, character.getMat());
			Imgproc.matchTemplate(mat, character.getMat(), result, Variables.METHOD);

			Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
			Point matchLoc = Core.minMaxLoc(result).maxLoc;

			Rect rect = new Rect((int) matchLoc.x, (int) matchLoc.y, 64, 64);
			Mat tile = screenshot.submat(rect);

			Imgproc.matchTemplate(character.getMat(), tile, result, Variables.METHOD);

			matchLoc = Core.minMaxLoc(result).maxLoc;
			rect = new Rect((int) matchLoc.x, (int) matchLoc.y, 64, 64);

			Mat hist = calculateHistogramm(screenshot.submat(rect));

			Double diff = Imgproc.compareHist(character.getHistogramm(), hist, 2);

			if (diff > 0) {

				if (diff > bestValue) {
					bestValue = diff;
					bestCharacter = character;
					// System.out.println(bestCharacter.getName());
				}

				if (saveResult) {
					Tools.saveBufferedImage(createResultImage(matchLoc, mat, character.getMat()),
							Variables.RESULTPATH + character.getName() + ".png");
				}
			}

		}

		return bestCharacter;

	}

	public static Character featureMatchingSimple(File file) {

		Mat source = Imgcodecs.imread(file.getAbsolutePath());
		Character bestCharacter = null;
		Double bestValue = 1d;

		for (Character character : characters) {
			Mat template = character.getMat();

			Mat resultMatrix = new Mat();
			int result_cols = source.cols() - template.cols() + 1;
			int result_rows = source.rows() - template.rows() + 1;
			resultMatrix.create(result_rows, result_cols, CvType.CV_32FC1);
			Imgproc.matchTemplate(source, template, resultMatrix, Imgproc.TM_SQDIFF_NORMED);

			MinMaxLocResult mmr = Core.minMaxLoc(resultMatrix);

			if (bestValue > mmr.minVal) {
				bestCharacter = character;
				bestValue = mmr.minVal;
			}
		}

		return bestCharacter != null ? bestCharacter : new Character(null, null, "None");
	}

	public static Character getCharacterFromTemplate(File file, boolean saveResult) {

		Mat screenshot = Imgcodecs.imread(file.getAbsolutePath());
		Character bestCharacter = null;
		Double bestValue = 0d;

		for (Character character : characters) {

			Mat mat = new Mat();
			screenshot.copyTo(mat);

			Mat result = prepareResult(mat, character.getMat());
			Imgproc.matchTemplate(mat, character.getMat(), result, Variables.METHOD);

			Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
			Point matchLoc = Core.minMaxLoc(result).maxLoc;

			Rect rect = new Rect((int) matchLoc.x, (int) matchLoc.y, 64, 64);
			Mat hist = calculateHistogramm(screenshot.submat(rect));

			Double diff = Imgproc.compareHist(character.getHistogramm(), hist, 2);

			if (diff > 0) {

				if (diff > bestValue) {
					bestValue = diff;
					bestCharacter = character;
					System.out.println(character.getName() + ": " + diff);

				}

				if (saveResult) {
					Tools.saveBufferedImage(createResultImage(matchLoc, mat, character.getMat()),
							Variables.RESULTPATH + character.getName() + ".png");
				}
			}

		}

		System.out.println("\n");

		return bestCharacter;

	}

	public static void init() {

		Tools.clearFolder(new File(Variables.RESULTPATH));
		Tools.resizeImages(Variables.CHARACTERPATH, 64);

		for (File character : new File(Variables.CHARACTERPATH).listFiles()) {

			if (!character.isDirectory()) {

				Mat characterMat = Imgcodecs.imread(character.getAbsolutePath());
				characters.add(new Character(characterMat, calculateHistogramm(characterMat),
						FilenameUtils.getBaseName(character.getName())));
			}
		}
	}

	private static BufferedImage createResultImage(Point matchLoc, Mat screenshot, Mat template) {

		Imgproc.rectangle(screenshot, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()),
				new Scalar(0, 255, 0), 2, 8, 0);

		return Tools.toBufferedImage(HighGui.toBufferedImage(screenshot));
	}

	private static Mat prepareResult(Mat screenshot, Mat template) {
		Mat result = new Mat();
		int columns = screenshot.cols() - template.cols() + 1;
		int rows = screenshot.rows() - template.rows() + 1;
		result.create(rows, columns, CvType.CV_32FC1);

		return result;
	}

	private static Mat calculateHistogramm(Mat mat) {

		Mat histogramm = new Mat();
		Mat histgrammResult = new Mat();
		mat.copyTo(histogramm);

		Imgproc.cvtColor(mat, histogramm, Imgproc.COLOR_RGB2RGBA);

		List<Mat> hsvTest1List = Arrays.asList(histogramm);
		Imgproc.calcHist(hsvTest1List, new MatOfInt(Variables.HISTOGRAMMCHANNELS), new Mat(), histgrammResult,
				new MatOfInt(Variables.HISTOGRAMMSIZE), new MatOfFloat(Variables.HISTOGRAMMRANGE), true);

		Mat histImage = new Mat(512, 400, CvType.CV_8UC3, new Scalar(0, 0, 0));

		Core.normalize(histgrammResult, histgrammResult, 0, histImage.rows(), Core.NORM_MINMAX);

		return histgrammResult;
	}

}
