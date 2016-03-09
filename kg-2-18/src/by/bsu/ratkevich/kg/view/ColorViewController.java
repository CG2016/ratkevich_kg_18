package by.bsu.ratkevich.kg.view;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ColorViewController {

	private static final String EYE_DROPPER_LABEL = "Пипетка (клавиша ALT): ";

	@FXML
	private ImageView imageSrc;

	@FXML
	private ImageView imageTarget;

	@FXML
	private Button uploadButton;

	@FXML
	private Button saveButton;

	@FXML
	private CheckBox isRgb;

	@FXML
	private ColorPicker fromColor;

	@FXML
	private Slider radiusSlider;

	@FXML
	private ColorPicker toColor;

	@FXML
	private CheckBox isAuto;

	@FXML
	private Button calculateBtn;

	@FXML
	private Label eyeDropperStatus;

	private Scene scene;

	private boolean eyeDropperCursor;

	@FXML
	void initialize() throws FileNotFoundException {

		final ChangeListener<Color> colorChangeListener = (observable, oldValue, newValue) -> {
			if (!isAuto.isSelected()) {
				return;
			}
			filterImage();
		};
		final ChangeListener<Number> changeListener = new ChangeListener<Number>() {

			private boolean isChanged = false;

			@Override
			public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) {
				if (isChanged || !isAuto.isSelected()) {
					return;
				}
				isChanged = true;
				try {
					filterImage();
				} finally {
					isChanged = false;
				}
			}
		};

		fromColor.valueProperty().addListener(colorChangeListener);
		toColor.valueProperty().addListener(colorChangeListener);
		isRgb.selectedProperty().addListener((observable, oldValue, newValue) -> filterImage());
		radiusSlider.valueProperty().addListener(changeListener);
		calculateBtn.setOnMouseClicked(event -> filterImage());
		isAuto.selectedProperty().addListener((observable, oldValue, newValue) -> {
			calculateBtn.setVisible(!calculateBtn.isVisible());
		});
	}

	public void filterImage() {
		if (imageSrc.getImage() == null) {
			return;
		}
		final ImageFilter colorfilter = new RGBSwapFilter(fromColor.getValue(), toColor.getValue(), radiusSlider.getValue(),
				isRgb.isSelected());
		final java.awt.Image imageFiltered = new Component() {

			private static final long serialVersionUID = 1L;
		}.createImage(new FilteredImageSource(SwingFXUtils.fromFXImage(imageSrc.getImage(), null).getSource(), colorfilter));

		imageTarget.setImage(SwingFXUtils.toFXImage(toBufferedImage(imageFiltered), null));
	}

	public static BufferedImage toBufferedImage(final java.awt.Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		final BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		final Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		return bimage;
	}

	@FXML
	public void uploadOnClick() throws FileNotFoundException {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Image");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image", "*.jpg", "*.jpeg", "*.png"));
		final File srcFileName = fileChooser.showOpenDialog(new Stage());
		if (srcFileName != null) {
			imageSrc.setImage(new Image(new BufferedInputStream(new FileInputStream(srcFileName))));
		}
	}

	@FXML
	public void saveOnClick() throws AWTException {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Image");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG Image", "*.png"));
		final File file = fileChooser.showSaveDialog(new Stage());
		if (file != null) {
			try {
				final Image image = imageTarget.getImage();
				if (image == null) {
					return;
				}
				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			} catch (final IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	@FXML
	public void windowKeyPressedListener(KeyEvent keyEvent) throws AWTException {
		if (keyEvent.getCode().compareTo(KeyCode.ALT) == 0) {
			if (eyeDropperCursor) {
				eyeDropperCursor = false;
				scene.setOnMouseMoved(null);
				eyeDropperStatus.setText(EYE_DROPPER_LABEL + "ВЫКЛ");
			} else {
				try {
					try {
						final Robot robot = new Robot();
						scene.setOnMouseMoved(mouseEvent -> {
							final Point pointer = MouseInfo.getPointerInfo().getLocation();
							final java.awt.Color pixelColor = robot.getPixelColor((int) pointer.getX(), (int) pointer.getY());
							fromColor.setValue(Color.rgb(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue()));
						});
						eyeDropperStatus.setText(EYE_DROPPER_LABEL + "ВКЛ");
					} catch (AWTException ex) {
						ex.printStackTrace();
					}
				} finally {
					eyeDropperCursor = true;
				}
			}
		}
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

}
