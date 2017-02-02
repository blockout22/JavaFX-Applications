package paint;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class PaintView extends BorderPane {

	private SimpleDoubleProperty widthProperty = new SimpleDoubleProperty();
	private SimpleDoubleProperty heightProperty = new SimpleDoubleProperty();

	private StackPane canvasPane;
	private ArrayList<Canvas> layers = new ArrayList<Canvas>();
	private VBox layerBox;
	private Button addLayer;
	private Rectangle rect;
	private Image image;
	private ImagePattern imagePattern;

	private PaintTools tools;

	public PaintView() {
		widthProperty.set(400);
		heightProperty.set(400);
		canvasPane = new StackPane();
		layerBox = new VBox();
		addLayer = new Button("Add Layer");
		tools = new PaintTools();

		addLayer.setOnAction(e -> {
			layers.add(addLayer());
			updateLayerBox();
			updateStackPane();
		});

		rect = new Rectangle(widthProperty.get() + 2, heightProperty.get() + 2);
		try {
			File file = new File(PaintView.class.getClassLoader().getResource("transparent.jpg").toURI());
			image = new Image(file.toURI().toString());
			imagePattern = new ImagePattern(image);
			rect.setFill(imagePattern);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		layers.add(layers.size(), addLayer());
		updateStackPane();
		updateLayerBox();
		setCenter(canvasPane);
		setRight(layerBox);
		setTop(tools);

		tools.bindLayers(layers);
	}

	private void updateStackPane() {
		canvasPane.getChildren().clear();

		if (layers.size() > 0) {
			rect.setStroke(Color.BLACK);
			canvasPane.getChildren().add(rect);
		}else{
			rect.setStroke(Color.RED);
			Text text = new Text("No Canvas");
			int height = (int) heightProperty.get() / 2 / 2 / 2;
			text.setStyle("-fx-font:" + height +  " arial;");
			text.setFill(Color.RED);
			canvasPane.getChildren().add(rect);
			canvasPane.getChildren().add(text);
		}

		for (int i = layers.size() - 1; i > -1; i--) {
			System.out.println(i);
			canvasPane.getChildren().add(layers.get(i));
		}
	}

	private void updateLayerBox() {
		layerBox.getChildren().clear();
		layerBox.getChildren().add(addLayer);
		for (int i = 0; i < layers.size(); i++) {
			HBox hbox = new HBox();
			CheckBox check = new CheckBox("layer " + (i + 1));
			Button delete = new Button("X");
			check.setSelected(true);
			int layerIndex = i;
			hbox.getChildren().addAll(check, delete);
			layerBox.getChildren().add(hbox);
			delete.setOnAction(e -> {
				layers.remove(layerIndex);
				layerBox.getChildren().remove(hbox);
				updateStackPane();
				updateLayerBox();
			});

			Canvas c = layers.get(i);

			check.setOnAction(e -> {
				if (!check.isSelected()) {
					c.setVisible(false);
				} else {
					c.setVisible(true);
				}
			});
		}
	}

	private Canvas addLayer() {
		Canvas canvas = new Canvas(widthProperty.get(), heightProperty.get());
		GraphicsContext gc = canvas.getGraphicsContext2D();
		canvas.setPickOnBounds(false);
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		// gc.setFill(Color.TRANSPARENT);
		gc.setStroke(tools.getColor());

		canvas.setOnMousePressed(e -> {
			if (tools.getSelectedTool() == PaintTools.RUBBER) {
				// gc.clearRect(x, y, w, h);
				gc.clearRect(e.getX() - (tools.getBurshSize() / 2), e.getY() - (tools.getBurshSize() / 2), tools.getBurshSize(), tools.getBurshSize());
			} else {
				gc.setLineWidth(tools.getBurshSize());
				gc.setStroke(tools.getColor());
				gc.beginPath();
				gc.moveTo(e.getX(), e.getY());
				gc.lineTo(e.getX(), e.getY());
				gc.stroke();
			}
		});

		canvas.setOnMouseDragged(e -> {
			if (tools.getSelectedTool() == PaintTools.RUBBER) {
				gc.clearRect(e.getX() - (tools.getBurshSize() / 2), e.getY() - (tools.getBurshSize() / 2), tools.getBurshSize(), tools.getBurshSize());
			} else {
				gc.lineTo(e.getX(), e.getY());
				gc.stroke();
			}
		});

		canvas.setOnMouseReleased(e -> {
			gc.closePath();
			// Random r = new Random();
			// widthProperty.set(r.nextInt(1000));
			// heightProperty.set(r.nextInt(1000));
			// canvas.resize(widthProperty.get(), heightProperty.get());
		});

		// getChildren().add(canvas);

		return canvas;
	}

}
