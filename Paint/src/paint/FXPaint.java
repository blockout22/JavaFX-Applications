package paint;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FXPaint extends Application {
	
	private PaintView pv;
	private Scene scene;

	@Override
	public void start(Stage stage) throws Exception {
		pv = new PaintView();
		scene = new Scene(pv, 600, 600, Color.GREY);
		
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
