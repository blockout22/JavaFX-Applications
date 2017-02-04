package paint;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
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

	private ScrollPane canvasSP;
	private StackPane canvasPane;
	private ArrayList<Canvas> layers = new ArrayList<Canvas>();
	private VBox layerBox;
	private Button addLayer;
	private Rectangle rect;
	private Image image;
	private ImagePattern imagePattern;

	private PaintTools tools;
	private boolean CONTROL_DOWN = false;
	private double CANVAS_SCALE = 1.0;

	public PaintView() {
		widthProperty.set(400);
		heightProperty.set(400);
		canvasSP = new ScrollPane();
		canvasPane = new StackPane();
		layerBox = new VBox();
		addLayer = new Button("Add Layer");
		tools = new PaintTools();
		
		canvasPane.minWidthProperty().bind(Bindings.createDoubleBinding(() -> 
			canvasSP.getViewportBounds().getWidth(), canvasSP.viewportBoundsProperty()));

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
		
		canvasPane.setFocusTraversable(true);
		canvasPane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>(){
			public void handle(KeyEvent event) {
				if(event.isControlDown())
				{
					CONTROL_DOWN = true;
				}else{
					CONTROL_DOWN = false;
				}
			}
		});
		
		canvasPane.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>(){
			public void handle(KeyEvent event) {
				if(event.isControlDown())
				{
					CONTROL_DOWN = true;
				}else{
					CONTROL_DOWN = false;
				}
			}
		});
		
		canvasPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>(){
			public void handle(ScrollEvent event) {
				System.out.println("SCROLL: " + CONTROL_DOWN + " : " + event.getDeltaY());
				if(CONTROL_DOWN){
					if(event.getDeltaY() > 0)
					{
						CANVAS_SCALE += 0.1;
					}else{
						if(CANVAS_SCALE < 0.2)
						{
							return;
						}
						CANVAS_SCALE -= 0.1;
					}
					System.out.println(CANVAS_SCALE);
					canvasPane.setScaleX(CANVAS_SCALE);
					canvasPane.setScaleY(CANVAS_SCALE);
				}
			}
		});

		layers.add(layers.size(), addLayer());
		updateStackPane();
		updateLayerBox();
		canvasSP.setContent(canvasPane);
		setCenter(canvasSP);
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
		MenuItem paste = new MenuItem("paste");
		ContextMenu menu = new ContextMenu(paste);
		GraphicsContext gc = canvas.getGraphicsContext2D();
//		gc.setFontSmoothingType(FontSmoothingType.);
//		gc.al
		canvas.setPickOnBounds(false);
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		// gc.setFill(Color.TRANSPARENT);
		gc.setStroke(tools.getColor());
		
		paste.setOnAction(e -> {
			Clipboard clipboard = Clipboard.getSystemClipboard();
			Image image = clipboard.getImage();
//			System.out.println(image.getHeight());
			gc.drawImage(image, 0, 0);
		});
		
		canvas.setOnContextMenuRequested(e -> {
			menu.show(this, e.getScreenX(), e.getScreenY());
		});

		canvas.setOnMousePressed(e -> {
			gc.setStroke(tools.getColor());
			if (tools.getSelectedTool() == PaintTools.RUBBER) {
				// gc.clearRect(x, y, w, h);
				gc.clearRect(e.getX() - (tools.getBurshSize() / 2), e.getY() - (tools.getBurshSize() / 2), tools.getBurshSize(), tools.getBurshSize());
			}else if(tools.getSelectedTool() == PaintTools.TEXT){
				gc.setFont(tools.getTextArgs().getFont());
//				gc.setStroke(tools.getTextArgs().getColor());
				gc.strokeText(tools.getTextArgs().getText(), Math.floor(e.getX()), Math.floor(e.getY()));
			}else {
				gc.setLineWidth(tools.getBurshSize());
//				gc.setStroke(tools.getColor());
				gc.beginPath();
				gc.moveTo(Math.floor(e.getX()), Math.floor(e.getY()));
				gc.lineTo(Math.floor(e.getX()), Math.floor(e.getY()));
				gc.stroke();
			}
		});

		canvas.setOnMouseDragged(e -> {
			if (tools.getSelectedTool() == PaintTools.RUBBER) {
				gc.clearRect(Math.floor(e.getX()) - (tools.getBurshSize() / 2), Math.floor(e.getY()) - (tools.getBurshSize() / 2), tools.getBurshSize(), tools.getBurshSize());
			}else if(tools.getSelectedTool() == PaintTools.TEXT){
				System.out.println("selected");
				gc.strokeText(tools.getTextArgs().getText(), Math.floor(e.getX()), Math.floor(e.getY()));
			} else {
				gc.lineTo(Math.floor(e.getX()), Math.floor(e.getY()));
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
