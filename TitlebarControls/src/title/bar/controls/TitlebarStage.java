package title.bar.controls;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TitlebarStage extends BorderPane {

	private BorderPane topBar;
	private HBox controls;
	private HBox cmm;

	private Button close;
	private Button maximise;
	private Button minise;
	
	private double xOffset;
	private double yOffset;

	public TitlebarStage(Stage stage) {
		stage.initStyle(StageStyle.UNDECORATED);

		topBar = new BorderPane();
		topBar.setFocusTraversable(true);
		controls = new HBox();
		topBar.setStyle("-fx-background-color:RED;");
		cmm = new HBox();

		minise = new Button("-");
		maximise = new Button("[]");
		close = new Button("X");

		minise.setOnAction(e -> {
			stage.setIconified(true);
		});

		maximise.setOnAction(e -> {
			if (stage.isMaximized()) {
				stage.setMaximized(false);
			} else {
				stage.setMaximized(true);
			}
		});

		close.setOnAction(e -> {
			stage.close();
		});
		
		topBar.setOnMousePressed(e -> {
			xOffset = stage.getX() - e.getScreenX();
			yOffset = stage.getY() - e.getScreenY();
			System.out.println(xOffset);
		});
		
		topBar.setOnMouseDragged(e -> {
			stage.setX(e.getScreenX() + xOffset);
			stage.setY(e.getScreenY() + yOffset);
		});

		setTop(topBar);
		topBar.setRight(cmm);
		cmm.getChildren().addAll(minise, maximise, close);
		// setTop(close);
	}

	public void setContent(Parent parent) {
		setCenter(parent);
	}

	public void addControl(Node node) {
		controls.getChildren().add(node);
	}
}
