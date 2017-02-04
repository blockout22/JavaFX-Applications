package internal.frame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class FXInternalFrame extends Application {

	private BorderPane pane;
	private Scene scene;
	private InternalFrame frame, frame2;

	@Override
	public void start(Stage stage) throws Exception {
		pane = new BorderPane();
		scene = new Scene(pane, 600, 600);
		frame = new InternalFrame(700, 700, "Internal Frame");
		frame2 = new InternalFrame(400, 400, "Smaller Frame");

		ImageView imageView = new ImageView("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Cheetah4.jpg/250px-Cheetah4.jpg");
		frame.setContent(imageView);

		pane.setCenter(frame2);
		pane.setBottom(frame);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
