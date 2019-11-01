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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import com.sonardraft.db.Character;
import com.sonardraft.db.Draft;
import com.sonardraft.db.enums.Role;
import com.sun.jna.platform.WindowUtils;

public class Tools {

	private Tools() {

	}

	public static boolean clientRunning = false;
	public static final boolean programmRunning = true;

	public static Draft getDraft() {

		List<Mat> screenshots = Tools.takeScreenshots(false);
		Draft draft = new Draft();

		int counter = 0;

		for (Mat mat : screenshots) {

			Character character = TemplateRecognition.featureMatchingSimple(mat);

			if (counter > 4) {
				draft.getRed().getPicks().add(character);
			} else if (counter < 5) {
				draft.getBlue().getPicks().add(character);
			}

			counter++;
		}

		return draft;
	}

	public static Draft getPriorityDraft(Draft draft) {

		// Draft logic

		List<Character> available = new ArrayList<>();
		available.addAll(Variables.characters);

		// Remove banned and picked

		for (Character character : draft.getBlue().getBanns()) {
			available.remove(character);
		}

		for (Character character : draft.getRed().getBanns()) {
			available.remove(character);
		}

		for (Character character : draft.getBlue().getPicks()) {
			available.remove(character);
		}

		for (Character character : draft.getRed().getPicks()) {
			available.remove(character);
		}

		// Calculate
		draft.getBlue().getCombos().addAll(cloneList(available));
		draft.getRed().getCombos().addAll(cloneList(available));

		// Check for combos
		for (Character character : draft.getBlue().getPicks()) {
			for (Character possibleCombo : character.getPriorities()) {

				// If character is available and add the prio
				Character foundCombo = findByName(draft.getBlue().getCombos(), possibleCombo.getName());

				if (foundCombo != null) {
					foundCombo.setPriority(foundCombo.getPriority() + possibleCombo.getPriorityBonus());

					if (foundCombo.getPriority() > 100) {
						foundCombo.setPriority(100);
					}
				}
			}
		}

		for (Character character : draft.getRed().getPicks()) {
			for (Character possibleCombo : character.getPriorities()) {

				// If character is available and add the prio
				Character foundCombo = findByName(draft.getRed().getCombos(), possibleCombo.getName());

				if (foundCombo != null) {
					foundCombo.setPriority(foundCombo.getPriority() + possibleCombo.getPriorityBonus());

					if (foundCombo.getPriority() > 100) {
						foundCombo.setPriority(100);
					}
				}
			}
		}

		// Filter None and Picking
		draft.getBlue().getCombos().remove(findByName(draft.getBlue().getCombos(), "None"));
		draft.getBlue().getCombos().remove(findByName(draft.getBlue().getCombos(), "Picking"));

		draft.getRed().getCombos().remove(findByName(draft.getRed().getCombos(), "None"));
		draft.getRed().getCombos().remove(findByName(draft.getRed().getCombos(), "Picking"));

		// Filter combos for roles

		draft.getBlue().setOpenRoles(remainingRoles(filterPickedRoles(draft.getBlue().getPicks())));
		draft.getRed().setOpenRoles(remainingRoles(filterPickedRoles(draft.getRed().getPicks())));
		draft.getBlue()
				.setCombos(filterRoles(draft.getBlue().getCombos(), filterPickedRoles(draft.getBlue().getPicks())));

		draft.getRed().setCombos(filterRoles(draft.getRed().getCombos(), filterPickedRoles(draft.getRed().getPicks())));

		// Order by priority
		Collections.sort(draft.getBlue().getCombos(), (o1, o2) -> o2.getPriority().compareTo(o1.getPriority()));
		Collections.sort(draft.getRed().getCombos(), (o1, o2) -> o2.getPriority().compareTo(o1.getPriority()));

		return draft;
	}

	private static List<Role> filterPickedRoles(List<Character> picks) {

		List<Role> remainingRoles = new ArrayList<>();
		EnumMap<Role, Integer> availableRoles = new EnumMap<>(Role.class);
		getAvailableRoles(availableRoles, picks);

		// If the remaining roles equal the characters which means that all roles have
		// to be taken
		if (availableRoles.size() == picks.size()) {
			remainingRoles.addAll(availableRoles.keySet());
		} else {
			int iterations = 0;
			while (iterations < 10) {
				for (Character character : picks) {
					if (!character.getRoles().isEmpty() && character.getRoles().size() == 1
							|| getSameRoles(character, picks).size() == picks.size() - 1) {
						remainingRoles.addAll(character.getRoles());
						removeRoles(character.getRoles(), picks);
					}
				}
				iterations++;
			}

		}

		return remainingRoles;

	}

	private static List<Character> getSameRoles(Character character, List<Character> characters) {

		List<Character> foundCharacters = new ArrayList<>();

		for (Character c : characters) {
			if (c.getRoles().size() == character.getRoles().size() && character.getRoles().containsAll(c.getRoles())) {
				foundCharacters.add(c);
			}
		}

		return foundCharacters;
	}

	private static void getAvailableRoles(EnumMap<Role, Integer> availableRoles, List<Character> characters) {

		for (Character character : characters) {

			for (Role role : character.getRoles()) {

				if (!availableRoles.containsKey(role)) {
					availableRoles.put(role, 0);
				}

				availableRoles.put(role, availableRoles.get(role) + 1);
			}
		}

	}

	private static void removeRoles(List<Role> roles, List<Character> characters) {

		for (Character character : characters) {
			character.getRoles().removeAll(roles);
		}
	}

	private static List<Role> remainingRoles(List<Role> roles) {
		List<Role> remainingRoles = new ArrayList<>(EnumSet.allOf(Role.class));
		remainingRoles.removeAll(roles);
		return remainingRoles;
	}

	private static List<Character> filterRoles(List<Character> combos, List<Role> roles) {

		List<Character> filtered = new ArrayList<>();

		for (Role role : remainingRoles(roles)) {
			filtered.addAll(combos.stream()
					.filter(character -> character.getRoles().contains(role) && !filtered.contains(character))
					.collect(Collectors.toList()));
		}

		return filtered;
	}

	public static List<Mat> takeScreenshots(boolean saveResult) {

		List<Mat> result = new ArrayList<>();

		if (isClientRunning()) {

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

								// Convert image to mat so we dont need to save it for performance
								result.add(BufferedImage2Mat(image));

								if (saveResult) {
									saveBufferedImage(image, Variables.SCREENPATH + a + i + ".png");
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		return result;
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

	public static boolean isClientRunning() {
		try {
			Runtime runtime = Runtime.getRuntime();
			String[] cmds = { "cmd", "/c", "tasklist" };
			Process proc = runtime.exec(cmds);
			InputStream inputstream = proc.getInputStream();
			InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
			BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
			String result = "";
			String line = "";
			while ((line = bufferedreader.readLine()) != null) {
				result += line;
			}

			return result.contains("LeagueClient.exe");
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public static Mat BufferedImage2Mat(BufferedImage image) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", byteArrayOutputStream);
		byteArrayOutputStream.flush();
		return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
	}

	public static ByteBuffer convertImageData(BufferedImage bi) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(bi, "png", out);
			return ByteBuffer.wrap(out.toByteArray());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Character findByName(Collection<Character> list, String name) {
		return list.stream().filter(character -> name.equals(character.getName())).findFirst().orElse(null);
	}

	public static List<Character> cloneList(List<Character> characters) {

		List<Character> clone = new ArrayList<>();

		for (Character character : characters) {
			clone.add(new Character(character));
		}

		return clone;
	}
}
