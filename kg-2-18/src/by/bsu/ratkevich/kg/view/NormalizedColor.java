package by.bsu.ratkevich.kg.view;

import javafx.scene.paint.Color;

public class NormalizedColor {

	private int alpha;

	private int red;

	private int green;

	private int blue;

	public NormalizedColor(Color color) {
		this.setAlpha((int) (color.getOpacity() * 255));
		this.setRed((int) (color.getRed() * 255));
		this.setGreen((int) (color.getGreen() * 255));
		this.setBlue((int) (color.getBlue() * 255));
	}

	public int getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public int getRGB() {
		int color = (alpha & 255) << 24;
		color |= (red & 255) << 16;
		color |= (green & 255) << 8;
		color |= (blue & 255);
		return color;
	}
}