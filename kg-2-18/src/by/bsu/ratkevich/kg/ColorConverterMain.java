package by.bsu.ratkevich.kg;

import java.io.IOException;

import by.bsu.ratkevich.kg.view.ColorViewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ColorConverterMain extends Application {

	private Stage primaryStage;

	private AnchorPane rootLayout;

    @Override
	public void start(final Stage primaryStage) {
        this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Color converter");

        initRootLayout();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
			final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ColorConverterMain.class.getResource("view/ColorView.fxml"));
			rootLayout = loader.<AnchorPane> load();

            // Show the scene containing the root layout.
			final Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

			// Give the controller access to the main app.
			ColorViewController controller = loader.getController();
			controller.setScene(scene);
            primaryStage.show();
		} catch (final IOException e) {
			e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

	public static void main(final String[] args) {
        launch(args);
    }

	@Override
	public void stop() throws Exception {
		super.stop();
		Platform.exit();
	}
}

