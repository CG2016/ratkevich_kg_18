package by.bsu.kg.ratkevich;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.IImageMetadata;

import javaxt.io.Image;

public class Main {

	private static final String IMAGE_FOLDER = "E:\\1\\lab3";

	//	private static final String IMAGE_FOLDER = "E:\\1";

	public static void main(final String[] args) {
		try {
			Files.walk(Paths.get(IMAGE_FOLDER))
					.filter(Files::isRegularFile)
					.forEach(path -> {
						try {
							ImageInfo imageInfo = Imaging.getImageInfo(path.toFile());
							System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
							System.out.println(String.format("%s; %dx%d; DPI: %dx%d; Compression: %s",
									path.getFileName(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getPhysicalWidthDpi(),
									imageInfo.getPhysicalHeightDpi(), imageInfo.getCompressionAlgorithm()));
							printEXIFData(path.toFile());
							IImageMetadata metadata = Imaging.getMetadata(path.toFile());
							if (metadata != null) {
								metadata.getItems()
										.forEach(System.out::println);
							}

							//							ImageInputStream stream = ImageIO.createImageInputStream(path.toFile());
							//							Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
							//
							//							if (readers.hasNext()) {
							//								ImageReader reader = readers.next();
							//								reader.setInput(stream);
							//
							//								IIOMetadata metadata = reader.getImageMetadata(0);
							//								IIOMetadataNode standardTree = (IIOMetadataNode) metadata
							//										.getAsTree(IIOMetadataFormatImpl.standardMetadataFormatName);
							//								NodeList nodeList = standardTree.getElementsByTagName("Dimension");
							//								IIOMetadataNode dimension = (IIOMetadataNode) (nodeList.getLength() > 0 ? nodeList.item(0) : null);
							//								double horizontalPixelSizeMM = getPixelSizeMM(dimension, "HorizontalPixelSize");
							//								double verticalPixelSizeMM = getPixelSizeMM(dimension, "VerticalPixelSize");
							//
							//								// TODO: Convert pixelsPerMM to DPI left as an exercise to the reader.. ;-)  
							//
							//								System.out.println(">>horizontalPixelSizeMM: " + horizontalPixelSizeMM);
							//								System.out.println(">>verticalPixelSizeMM: " + verticalPixelSizeMM);
							//							} else {
							//								System.err.printf("Could not read %s\n", path.getFileName());
							//							}
						} catch (IOException | ImageReadException ex) {
							ex.printStackTrace();
						}
					});
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	//	private static double getPixelSizeMM(final IIOMetadataNode dimension, final String elementName) {
	//		if (dimension == null) {
	//			return -1;
	//		}
	//		// NOTE: The standard metadata format has defined dimension to pixels per millimeters, not DPI...
	//		NodeList pixelSizes = dimension.getElementsByTagName(elementName);
	//		IIOMetadataNode pixelSize = pixelSizes.getLength() > 0 ? (IIOMetadataNode) pixelSizes.item(0) : null;
	//		return pixelSize != null ? Double.parseDouble(pixelSize.getAttribute("value")) : -1;
	//	}

	private static void printEXIFData(File file) {
		Image image = new Image(file);
		HashMap<Integer, Object> exif = image.getExifTags();
		//Print Camera Info
		System.out.println("-----------------------------");
		System.out.println("EXIF Fields: " + exif.size());
		System.out.println("-----------------------------");
		if (exif.size() != 0) {
			//			System.out.println("Date: " + exif.get(0x0132)); //0x9003       
			//			System.out.println("Camera: " + exif.get(0x0110));
			//			System.out.println("Manufacturer: " + exif.get(0x010F));
			//			System.out.println("Focal Length: " + exif.get(0x920A));
			//			System.out.println("F-Stop: " + exif.get(0x829D));
			System.out.println("Exposure Time (1 / Shutter Speed): " + exif.get(0x829A));
			System.out.println("ISO Speed Ratings: " + exif.get(0x8827));
			System.out.println("Shutter Speed Value (APEX): " + exif.get(0x9201));
			System.out.println("Shutter Speed (Exposure Time): " + exif.get(0x9201));
			System.out.println("Aperture Value (APEX): " + exif.get(0x9202));

			//Print Image Orientation

			//			Object orient = exif.get(0x0112);
			//			if (orient != null) {
			//				int orientation = (int) orient;
			//				String desc = "";
			//				switch (orientation) {
			//					case 1:
			//						desc = "Top, left side (Horizontal / normal)";
			//						break;
			//					case 2:
			//						desc = "Top, right side (Mirror horizontal)";
			//						break;
			//					case 3:
			//						desc = "Bottom, right side (Rotate 180)";
			//						break;
			//					case 4:
			//						desc = "Bottom, left side (Mirror vertical)";
			//						break;
			//					case 5:
			//						desc = "Left side, top (Mirror horizontal and rotate 270 CW)";
			//						break;
			//					case 6:
			//						desc = "Right side, top (Rotate 90 CW)";
			//						break;
			//					case 7:
			//						desc = "Right side, bottom (Mirror horizontal and rotate 90 CW)";
			//						break;
			//					case 8:
			//						desc = "Left side, bottom (Rotate 270 CW)";
			//						break;
			//				}
			//				System.out.println("Orientation: " + orientation + " -- " + desc);
			//			}
		}
		//Print GPS Information
		double[] coord = image.getGPSCoordinate();
		if (coord != null) {
			System.out.println("GPS Coordinate: " + coord[0] + ", " + coord[1]);
			System.out.println("GPS Datum: " + image.getGPSDatum());
		}
	}
}
