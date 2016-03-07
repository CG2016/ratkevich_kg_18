package by.bsu.ratkevich.kg.view;

import static java.lang.Math.pow;

import java.awt.image.RGBImageFilter;

import org.apache.commons.imaging.color.ColorCieLab;
import org.apache.commons.imaging.color.ColorConversions;

import javafx.scene.paint.Color;

public class RGBSwapFilter extends RGBImageFilter {

	private double radius;

	private Color fromColor;

	private Color toColor;

	private boolean hued;

	public RGBSwapFilter(Color fromColor, Color toColor, double radius, boolean hued) {
		this.fromColor = fromColor;
		this.toColor = toColor;
		this.radius = radius;
		this.setHued(hued);

		canFilterIndexColorModel = true;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		int realA = (rgb >> 24) & 255;

		if (colorDistance(rgb, fromColor) < radius) {
			if (isHued()) {
				int toR = (int) (toColor.getRed() * 255);
				int toG = (int) (toColor.getGreen() * 255);
				int toB = (int) (toColor.getBlue() * 255);
				int color = (realA & 255) << 24;
				color |= (toR & 255) << 16;
				color |= (toG & 255) << 8;
				color |= (toB & 255);
				return color;
			} else {
				ColorCieLab toColorLab = convertRGBtoLab(toColor);
				ColorCieLab fromColorLab = convertRGBtoLab(fromColor);
				ColorCieLab realColorLab = convertRGBtoLab(rgb);

				ColorCieLab toRealColorLab = new ColorCieLab(toColorLab.L + (realColorLab.L - fromColorLab.L),
						toColorLab.a + (realColorLab.a - fromColorLab.a), toColorLab.b + (realColorLab.b - fromColorLab.b));
				int toRealColorRgb = convertLabtoRGB(toRealColorLab);
				return toRealColorRgb;
			}
		} else {
			return rgb;
		}
	}

	private double colorDistance(int color1, Color color2) {
		final NormalizedColor color2Norm = new NormalizedColor(color2);
		final ColorCieLab colorLab1 = convertRGBtoLab(color1);
		final ColorCieLab colorLab2 = convertRGBtoLab(color2Norm);
		return Math.sqrt(pow(colorLab2.L - colorLab1.L, 2) / 3.0 + pow(colorLab2.a - colorLab1.a, 2) + pow(colorLab2.b - colorLab1.b, 2));
	}

	private ColorCieLab convertRGBtoLab(Color rgbColor) {
		return convertRGBtoLab(new NormalizedColor(rgbColor));
	}

	private ColorCieLab convertRGBtoLab(NormalizedColor rgbColor) {
		return convertRGBtoLab(rgbColor.getRGB());
	}

	private ColorCieLab convertRGBtoLab(int rgbColor) {
		return ColorConversions.convertXYZtoCIELab(ColorConversions.convertRGBtoXYZ(rgbColor));
	}

	private int convertLabtoRGB(ColorCieLab labColor) {
		return ColorConversions.convertXYZtoRGB(ColorConversions.convertCIELabtoXYZ(labColor));
	}

	public boolean isHued() {
		return hued;
	}

	public void setHued(boolean hued) {
		this.hued = hued;
	}
}