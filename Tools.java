package com.sonardraft;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tools {

	private static final String CHARACTERURL = "https://na.leagueoflegends.com/en/game-info/champions/";

	private Tools() {

	}

	public static void grabCharacterImages() {

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

	public static void saveBufferedImage(BufferedImage image, String path) {
		try {
			File outputfile = new File(path);
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
