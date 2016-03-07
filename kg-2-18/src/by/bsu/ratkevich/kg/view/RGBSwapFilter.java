package by.bsu.ratkevich.kg.view;

import static java.lang.Math.pow;

import java.awt.image.RGBImageFilter;

import org.apache.commons.imaging.color.ColorCieLab;
import org.apache.commons.imaging.color.ColorConversions;

import javafx.scene.paint.Color;

public class RGBSwapFilter extends RGBImageFilter {

	private final double radius;

	private final Color fromColor;

	private final Color toColor;

	private boolean hued;

	public RGBSwapFilter(final Color fromColor, final Color toColor, final double radius, final boolean hued) {
		this.fromColor = fromColor;
		this.toColor = toColor;
		this.radius = radius;
		this.setHued(hued);

		canFilterIndexColorModel = true;
	}

	@Override
	public int filterRGB(final int x, final int y, final int rgb) {
		final int realA = (rgb >> 24) & 255;

		if (colorDistance(rgb, fromColor) < radius) {
			if (isHued()) {
				final int toR = (int) (toColor.getRed() * 255);
				final int toG = (int) (toColor.getGreen() * 255);
				final int toB = (int) (toColor.getBlue() * 255);
				int color = (realA & 255) << 24;
				color |= (toR & 255) << 16;
				color |= (toG & 255) << 8;
				color |= (toB & 255);
				return color;
			} else {
				final ColorCieLab toColorLab = convertRGBtoLab(toColor);
				final ColorCieLab fromColorLab = convertRGBtoLab(fromColor);
				final ColorCieLab realColorLab = convertRGBtoLab(rgb);

				final ColorCieLab toRealColorLab = new ColorCieLab(toColorLab.L + (realColorLab.L - fromColorLab.L),
						toColorLab.a + (realColorLab.a - fromColorLab.a), toColorLab.b + (realColorLab.b - fromColorLab.b));
				final int toRealColorRgb = convertLabtoRGB(toRealColorLab);
				return toRealColorRgb;
			}
		} else {
			return rgb;
		}
	}

	private double colorDistance(final int color1, final Color color2) {
		final NormalizedColor color2Norm = new NormalizedColor(color2);
		final ColorCieLab colorLab1 = convertRGBtoLab(color1);
		final ColorCieLab colorLab2 = convertRGBtoLab(color2Norm);
		return Math.sqrt(pow(colorLab2.L - colorLab1.L, 2) / 3.0 + pow(colorLab2.a - colorLab1.a, 2) + pow(colorLab2.b - colorLab1.b, 2));
	}

	private ColorCieLab convertRGBtoLab(final Color rgbColor) {
		return convertRGBtoLab(new NormalizedColor(rgbColor));
	}

	private ColorCieLab convertRGBtoLab(final NormalizedColor rgbColor) {
		return convertRGBtoLab(rgbColor.getRGB());
	}

	private ColorCieLab convertRGBtoLab(final int rgbColor) {
		return ColorConversions.convertXYZtoCIELab(ColorConversions.convertRGBtoXYZ(rgbColor));
	}

	private int convertLabtoRGB(final ColorCieLab labColor) {
		return ColorConversions.convertXYZtoRGB(ColorConversions.convertCIELabtoXYZ(labColor));
	}

	public boolean isHued() {
		return hued;
	}

	public void setHued(final boolean hued) {
		this.hued = hued;
	}
}