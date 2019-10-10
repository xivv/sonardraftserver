package com.sonardraft;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import com.sun.jna.platform.WindowUtils;

public class Tools {

	private Tools() {

	}

	public static boolean clientRunning = false;

	public static void start() {

		System.out.println("Waiting for League of Legends to start...");

		while (clientRunning = false) {
			// Listen for League of Legends to start
			WindowUtils.getAllWindows(true).forEach(desktopWindow -> {

				if (desktopWindow.getTitle().equals("League of Legends")) {

					clientRunning = true;
				}
			});
		}

		System.out.println("Listening for champion picks...");
	}

	public static void main(String[] args) throws AWTException {

		WindowUtils.getAllWindows(true).forEach(desktopWindow -> {

			// This is only for 1920x1080
			if (desktopWindow.getTitle().equals("League of Legends")) {
				System.out.println(desktopWindow.getLocAndSize());

				// 10 x 25 - 215 x 60 Team 1 Bann
				// 1070 x 25 - 1270 x 60 Team 2 Bann

				// 40 x 85 - 75 x 600 Team 1 Pick
				// 1170 x 85 - 1205 x 85 Team 2 Pick

				try {
					Robot robot = new Robot();

					Point base = new Point(desktopWindow.getLocAndSize().x, desktopWindow.getLocAndSize().y);

					Rectangle team1Bann = new Rectangle(base.x + 10, base.y + 25, 205, 45);
					Rectangle team2Bann = new Rectangle(base.x + 1070, base.y + 25, 205, 45);

					Rectangle team1Pick = new Rectangle(base.x + 0, base.y + 90, 150, 400);
					Rectangle team2Pick = new Rectangle(base.x + 1180, base.y + 90, 100, 400);

					BufferedImage bann1 = robot.createScreenCapture(team1Bann);
					BufferedImage bann2 = robot.createScreenCapture(team2Bann);
					BufferedImage pick1 = robot.createScreenCapture(team1Pick);
					BufferedImage pick2 = robot.createScreenCapture(team2Pick);

					saveBufferedImage(bann1, Variables.SCREENPATH + "bann1.png");
					saveBufferedImage(bann2, Variables.SCREENPATH + "bann2.png");
					saveBufferedImage(pick1, Variables.SCREENPATH + "pick1.png");
					saveBufferedImage(pick2, Variables.SCREENPATH + "pick2.png");

				} catch (AWTException e) {
					e.printStackTrace();
				}

			}
		});

	}

	public static void resizeImages(String path, Integer size) {

		File folder = new File(path);

		for (File file : folder.listFiles())
			if (!file.isDirectory() && FilenameUtils.isExtension(file.getName(), Variables.IMAGEFORMATS)) {

				BufferedImage image = loadBufferedImage(file.getAbsolutePath());

				if (image.getHeight() != size) {
					BufferedImage resizedImage = resize(image, size, size);
					saveBufferedImage(resizedImage, file.getAbsolutePath());
				}
			}

	}

	public static void clearFolder(File folder) {
		for (File file : folder.listFiles())
			if (!file.isDirectory()) {
				file.delete();
			}
	}

	public static BufferedImage toBufferedImage(Image image) {

		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(image, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;

	}

	private static BufferedImage resize(BufferedImage img, int height, int width) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
	}

	public static BufferedImage loadBufferedImage(String path) {

		try {
			File bufferedImage = new File(path);
			return ImageIO.read(bufferedImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveBufferedImage(BufferedImage image, String path) {
		try {
			File outputfile = new File(path);
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
