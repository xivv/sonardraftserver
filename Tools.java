package com.sonardraft;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

public class Tools {

	/**
	 * Konfiguration
	 */
	private static final String CHARACTERURL = "https://na.leagueoflegends.com/en/game-info/champions/";

	private static final String[] IMAGEFORMATS = { "png" };

	private Tools() {

	}

	public static void grabCharacterImages() {

	}

	public static void resizeImages(String path, Integer size) {

		File folder = new File(path);

		for (File file : folder.listFiles())
			if (!file.isDirectory() && FilenameUtils.isExtension(file.getName(), IMAGEFORMATS)) {

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
