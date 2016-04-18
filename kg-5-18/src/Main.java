import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.knowm.xchart.Chart;
import org.knowm.xchart.Series;
import org.knowm.xchart.SeriesMarker;
import org.knowm.xchart.StyleManager.ChartType;
import org.knowm.xchart.SwingWrapper;

public class Main {

	private static final int WIDTH = 800;

	private static final int HEIGHT = 600;

	public static void main(String[] args) {
		int radius = 20;
		Point pointA = new Point(-0, -0);
		Point pointB = new Point(11, 13);

		List<Point> rasterizeBresenham = rasterizeBresenham(pointA, pointB);
		List<Point> rasterizeDDA = rasterizeDDA(pointA, pointB);
		List<Point> rasterizeStep = rasterizeStep(pointA, pointB);
		List<Point> rasteriseCircle = rasteriseCircle(radius, pointB);

		System.out.println(rasterizeBresenham);
		System.out.println(rasterizeDDA);
		System.out.println(rasterizeStep);
		System.out.println(rasteriseCircle);

		Chart chart = new Chart(WIDTH, HEIGHT);
		chart.getStyleManager().setChartType(ChartType.Scatter);
		System.out.println(chart.getStyleManager().getMarkerSize());
		Series series = chart.addSeries("Bresenham", extractCoordinate(rasteriseCircle, Point::getX),
				extractCoordinate(rasteriseCircle, Point::getY));
		series.setMarker(SeriesMarker.SQUARE);
		new SwingWrapper(chart).displayChart();
	}

	public static List<Double> extractCoordinate(List<Point> points, Function<Point, Double> extractor) {

		return points.stream().map(extractor).collect(Collectors.toList());
	}

	public static List<Point> rasterizeDDA(Point pointA, Point pointB) {
		List<Point> pointList = new ArrayList<>();

		double deltaX = pointB.x - pointA.x;
		double deltaY = pointB.y - pointA.y;

		int stepsCount = (int) Math.max(Math.abs(deltaX), Math.abs(deltaY));

		double xInc = deltaX / stepsCount;
		double yInc = deltaY / stepsCount;

		double x = pointA.x;
		double y = pointA.y;
		for (int i = 0; i < stepsCount; i++) {
			pointList.add(new Point((int) x, (int) y));
			x += xInc;
			y += yInc;
		}
		pointList.add(pointB);
		return pointList;
	}

	public static List<Point> rasterizeBresenham(Point pointA, Point pointB) {
		List<Point> points = new ArrayList<>();
		int deltaX = Math.abs(pointB.x - pointA.x);
		int deltaY = Math.abs(pointB.y - pointA.y);
		int signY = pointA.y < pointB.y ? 1 : -1;
		int error = deltaX - deltaY;

		int x = pointA.x;
		int y = pointA.y;
		while (x < pointB.x || y < pointB.y) {
			points.add(new Point(x, y));
			if (error * 2 > -deltaY) {
				error -= deltaY;
				x++;
			}
			if (error * 2 < deltaX) {
				error += deltaX;
				y += signY;
			}
		}
		points.add(pointB);
		return points;
	}

	public static List<Point> rasterizeStep(Point pointA, Point pointB) {
		List<Point> points = new ArrayList<>();

		double k = (pointB.y - pointA.y) / (pointB.x - pointA.x);
		double b = pointA.y - pointA.x * k;

		points.add(pointA);
		for (int x = pointA.x; x < pointB.x; x++) {
			int y = (int) (k * x + b);
			points.add(new Point(x, y));
		}
		points.add(pointB);

		return points;
	}

	public static List<Point> rasteriseCircle(int radius, Point middlePoint) {
		List<Point> points = new ArrayList<>();
		int x = 0;
		int y = radius;
		int delta = 1 - radius * 2;
		int error;
		while (y >= 0) {
			points.add(new Point(middlePoint.x + x, middlePoint.y + y));
			points.add(new Point(middlePoint.x + x, middlePoint.y - y));
			points.add(new Point(middlePoint.x - x, middlePoint.y + y));
			points.add(new Point(middlePoint.x - x, middlePoint.y - y));

			error = 2 * (delta + y) - 1;
			if ((delta < 0) && (error <= 0)) {
				delta += 2 * ++x + 1;
				continue;
			}
			error = 2 * (delta - x) - 1;
			if ((delta > 0) && (error >= 0)) {
				delta += 1 - 2 * --y;
				continue;
			}
			x++;
			delta += 2 * (x - y);
			y--;
		}
		return points;
	}
}
