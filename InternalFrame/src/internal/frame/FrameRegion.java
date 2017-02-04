package internal.frame;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

public class FrameRegion extends Region{
	
	private BorderPane titleBar;
	private Label titleLabel;
	private BorderPane pane;

	protected FrameRegion(double width, double height, String title) {
		titleBar = new BorderPane();
		pane = new BorderPane();
		pane.snapToPixelProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				System.out.println(newValue);
			}});
		pane.setPrefSize(width, height);
		pane.setStyle("-fx-border-width: 1; -fx-border-color: black");

		titleBar.setStyle("-fx-background-color: #1048a3; -fx-padding: 3");

		titleLabel = new Label(title);
		Button closeButton = new Button("-");

		titleBar.setLeft(titleLabel);
		titleBar.setRight(closeButton);

		pane.setTop(titleBar);
		getChildren().add(pane);

		makeDragable(titleBar);
		makeDragable(titleLabel);
		setOnMouseClicked(mouseEvent -> {
			toFront();
		});
	}
	
	protected void setTitle(String title){
		titleLabel.setText(title);
	}

	private void makeDragable(Node child) {
		final Delta delta = new Delta();

		child.setOnMousePressed(e -> {
			delta.x = getLayoutX() - e.getScreenX();
			delta.y = getLayoutY() - e.getScreenY();
			toFront();
		});

		child.setOnMouseDragged(e -> {
			setLayoutX(e.getScreenX() + delta.x);
			setLayoutY(e.getScreenY() + delta.y);
		});
	}

	protected void setContent(Node node) {
		pane.setCenter(node);
	}

	public void makeResizable(double width) {
		setOnMouseMoved(e -> {
			double mouseX = e.getX();
			double mouseY = e.getY();

			double w = boundsInLocalProperty().get().getWidth();
			double h = boundsInLocalProperty().get().getHeight();

			if (Math.abs(mouseX) - w < width && Math.abs(mouseY - h) < width) {

			}
		});
	}

	private class Delta {
		double x, y;
	}

}
