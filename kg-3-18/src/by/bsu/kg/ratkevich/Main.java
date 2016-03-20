package by.bsu.kg.ratkevich;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import javax.imageio.ImageIO;

import org.knowm.xchart.Chart;
import org.knowm.xchart.ChartBuilder;
import org.knowm.xchart.SeriesColor;
import org.knowm.xchart.StyleManager.ChartType;
import org.knowm.xchart.StyleManager.LegendPosition;
import org.knowm.xchart.SwingWrapper;

public class Main {

	//	private static final String IMAGE_FILE = "E:\\12345.png";
	private static final String IMAGE_FILE = "E:\\123.jpg";
	//	private static final String IMAGE_FILE = "D:\\Фоны на рабочий стол\\audi-r8-car-red-vertolet.jpg";
	private static final int LENGTH = 256;

	private static int[] pixels = new int[LENGTH];
	static {
		for (int i = 0; i < LENGTH; i++) {
			pixels[i] = i;
		}
	}

	public static void main(final String[] args) {
		try {
			final BufferedImage image = ImageIO.read(new File(IMAGE_FILE));
			final int[] redPixels = new int[LENGTH];
			final int[] greenPixels = new int[LENGTH];
			final int[] bluePixels = new int[LENGTH];
			
			final double[] avgRGB = new double[3];
			long[] sum = new long[3];

			final int[] rgb = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

			for (int pixel : rgb) {
				final Color color = new Color(pixel);
				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();
				redPixels[red]++;
				greenPixels[green]++;
				bluePixels[blue]++;
				sum[0] += red ;
				sum[1] += green;
				sum[2] += blue;
			}

			for (int i = 0; i < sum.length; i++) {
				avgRGB[i] = (double) sum[i] / rgb.length;
			}

			showRGBchart(redPixels, greenPixels, bluePixels);
			rgbChart(redPixels, "RED", Color.RED);
			rgbChart(greenPixels, "GREEN", Color.GREEN);
			rgbChart(bluePixels, "BLUE", Color.BLUE);
			rgbAvgChart(avgRGB);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private static void showRGBchart(final int[] redPixels, final int[] greenPixels, final int[] bluePixels) {
		final Chart chart = new ChartBuilder().chartType(ChartType.Bar).width(800).height(600).title(IMAGE_FILE)
				.yAxisTitle("Pixels count").build();

		chart.getStyleManager().setLegendPosition(LegendPosition.InsideNE);
		chart.getStyleManager().setBarsOverlapped(true);
		chart.getStyleManager().setXAxisTicksVisible(false);
		chart.addSeries("RED", pixels, redPixels).setLineColor(SeriesColor.RED);
		chart.addSeries("GREEN", pixels, greenPixels).setLineColor(SeriesColor.GREEN);
		chart.addSeries("BLUE", pixels, bluePixels).setLineColor(SeriesColor.BLUE);
		new SwingWrapper(chart).displayChart();
	}

	private static void rgbChart(final int[] colorPixels, final String colorName, final Color color) {
		final Chart chart = new ChartBuilder().chartType(ChartType.Bar).width(800).height(600).title(IMAGE_FILE)
				.yAxisTitle("Pixels count").build();
		chart.getStyleManager().setLegendPosition(LegendPosition.InsideNE);
		chart.getStyleManager().setXAxisTicksVisible(false);
		chart.addSeries(colorName, pixels, colorPixels).setLineColor(color);
		new SwingWrapper(chart).displayChart();
	}

	private static void rgbAvgChart(final double[] avgRGB) {
		final Chart chart = new ChartBuilder().chartType(ChartType.Bar).width(800).height(600).title(IMAGE_FILE)
				.yAxisTitle("Avg RGB value").build();
		chart.addSeries("Average RGB", Arrays.asList("RED", "GREEN", "BLUE"), DoubleStream.of(avgRGB).boxed().collect(Collectors.toList()))
				.setLineColor(SeriesColor.PURPLE);
		new SwingWrapper(chart).displayChart();
	}
}
