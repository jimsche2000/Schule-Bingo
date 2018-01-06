package BingoToolkit;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageLoader {
	private static String folder = "img/";

	public static ImageIcon loadIcon(String name, int width, int height) {

		BufferedImage img = null;
		try {
			img = ImageIO.read(ClassLoader.getSystemResource(folder + name));
		} catch (IOException e) {
			System.out.println("ERR: Image \"" + name + "\" couldn't load:\n");
			e.printStackTrace();
		}
		Image dimg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(dimg);
	}

	public static ImageIcon loadIcon(String name) {

		BufferedImage img = null;
		try {
			img = ImageIO.read(ClassLoader.getSystemResource(folder + name));
		} catch (IOException e) {
			System.out.println("ERR: Image \"" + name + "\" couldn't load:\n");
			e.printStackTrace();
		}
		return new ImageIcon(img);
	}

	public static Image loadImage(String name, int width, int height) {

		BufferedImage img = null;
		try {
			img = ImageIO.read(ClassLoader.getSystemResource(folder + name));
		} catch (IOException e) {
			System.out.println("ERR: Image \"" + name + "\" couldn't load:\n");
			e.printStackTrace();
		}
		Image dimg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return dimg;
	}

	public static Image loadImage(String name) {

		BufferedImage img = null;
		try {
			img = ImageIO.read(ClassLoader.getSystemResource(folder + name));
		} catch (IOException e) {
			System.out.println("ERR: Image \"" + name + "\" couldn't load:\n");
			e.printStackTrace();
		}
		return (Image) img;
	}

	public static BufferedImage loadBufferedImage(String name, int width, int height) {

		BufferedImage img = null;
		try {
			img = ImageIO.read(ClassLoader.getSystemResource(folder + name));
		} catch (IOException e) {
			System.out.println("ERR: Image \"" + name + "\" couldn't load:\n");
			e.printStackTrace();
		}
		Image dimg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return (BufferedImage) dimg;
	}

	public static BufferedImage loadBufferedImage(String name) {

		BufferedImage img = null;
		try {
			img = ImageIO.read(ClassLoader.getSystemResource(folder + name));
		} catch (IOException e) {
			System.out.println("ERR: Image \"" + name + "\" couldn't load:\n");
			e.printStackTrace();
		}
		return img;
	}
}