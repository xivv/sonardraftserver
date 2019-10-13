package com.sonardraft;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import com.sonardraft.db.Character;
import com.sonardraft.db.Draft;
import com.sun.jna.platform.WindowUtils;

public class Tools {

	private Tools() {

	}

	public static boolean clientRunning = false;
	public static boolean programmRunning = true;
	public static Draft draft = new Draft();

	public static void start() {

		File screenPathDirectory = new File(Variables.SCREENPATH);

		while (programmRunning) {

			TemplateRecognition.init();

			System.out.println("Waiting for League of Legends to start...");
			while (clientRunning) {

				Tools.takeScreenshots();

				int counter = 0;
				draft.getRed().getPicks().clear();
				draft.getBlue().getPicks().clear();

				for (File file : screenPathDirectory.listFiles()) {

					Character character = TemplateRecognition.featureMatchingSimple(file);

					if (counter > 4) {
						draft.getRed().getPicks().add(character);
					} else if (counter < 5) {
						draft.getBlue().getPicks().add(character);
					}

					counter++;
				}

				System.out.println(draft.getBlue().toString());
				System.out.println(draft.getRed().toString());

				try {
					Thread.sleep(Variables.SCREENSHOTINTERVALL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public static void takeScreenshots() {

		WindowUtils.getAllWindows(true).forEach(desktopWindow -> {

			if (desktopWindow.getTitle().equals("League of Legends")) {

				// CHAR 1
				// 50x100 - 70x70 - 100
				try {
					Robot robot = new Robot();
					for (var a = 0; a < 2; a++) {

						Point base = new Point();

						if (a == 0) {
							base = new Point(desktopWindow.getLocAndSize().x + 10,
									desktopWindow.getLocAndSize().y + 100);
						} else if (a == 1) {
							base = new Point(desktopWindow.getLocAndSize().x + 1195,
									desktopWindow.getLocAndSize().y + 100);
						}

						for (var i = 0; i < 5; i++) {

							Rectangle rect = new Rectangle(base.x, base.y + i * 80, 110 - (a == 1 ? 40 : 0), 70);

							BufferedImage image = robot.createScreenCapture(rect);
							saveBufferedImage(image, Variables.SCREENPATH + a + i + ".png");
						}
					}
				} catch (Exception e) {
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

		try (AsynchronousFileChannel asyncFile = AsynchronousFileChannel.open(Paths.get(path), StandardOpenOption.WRITE,
				StandardOpenOption.CREATE)) {
			asyncFile.write(convertImageData(image), 0);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void isClientRunning() {
		try {
			Runtime runtime = Runtime.getRuntime();
			String cmds[] = { "cmd", "/c", "tasklist" };
			Process proc = runtime.exec(cmds);
			InputStream inputstream = proc.getInputStream();
			InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
			BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
			String result = "";
			String line = "";
			while ((line = bufferedreader.readLine()) != null) {
				result += line;
			}

			if (result.contains("LeagueClient.exe")) {
				clientRunning = true;
				Thread.sleep(5000);
			} else {
				clientRunning = false;
				Thread.sleep(2000);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Cannot query the tasklist for some reason.");
		}

	}

	public static ByteBuffer convertImageData(BufferedImage bi) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(bi, "png", out);
			return ByteBuffer.wrap(out.toByteArray());
		} catch (IOException ex) {
			// TODO
		}
		return null;
	}
}
