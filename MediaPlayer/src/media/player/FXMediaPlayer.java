package media.player;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FXMediaPlayer extends Application {

	private StackPane pane;
	private Scene scene;
	private PlayerPane player;

	@Override
	public void start(Stage stage) throws Exception {
		pane = new StackPane();
		scene = new Scene(pane, 1020, 800, Color.BLACK);
		pane.setStyle("-fx-background-color: rgb(0,0,0.0,0.0);");

		player = new PlayerPane();

		scene.widthProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//				 player.getMediaView().maxWidth(stage.getWidth());
				 player.getMediaView().setFitWidth(scene.getWidth());
			}
		});

		scene.heightProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				player.getMediaView().setFitHeight(newValue.doubleValue());
			}
		});

		pane.getChildren().addAll(player);
		stage.setScene(scene);
		stage.show();
		player.getMediaView().setFitHeight(scene.getHeight());
		player.getMediaView().setFitWidth(scene.getWidth());
	}

	public static void main(String[] args) {
		launch(args);
	}

}
