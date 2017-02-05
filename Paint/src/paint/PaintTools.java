package paint;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PaintTools extends BorderPane {

	private HBox topRow;
	private Button save;

	private HBox hbox;
	private ComboBox<Number> brush_size;
	private ToggleGroup buttonGroup;
	public static ToggleButton BRUSH, RUBBER, TEXT;
	public ColorPicker colorPicker;
	public Button changeBrightness;

	private ObservableList<Number> brushSizeList = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 125, 150, 175, 200, 225, 250, 275, 300, 325, 350, 375, 400, 425, 450, 475, 500);
	private ArrayList<Canvas> layers;
	
	private TextArguments textArgs;

	public PaintTools() {
		topRow = new HBox();
		save = new Button("Save");

		hbox = new HBox();
		brush_size = new ComboBox<Number>(brushSizeList);
		buttonGroup = new ToggleGroup();
		BRUSH = new ToggleButton("Brush");
		RUBBER = new ToggleButton("Rubber");
		TEXT = new ToggleButton("Text");
		colorPicker = new ColorPicker(Color.RED);
		changeBrightness = new Button("Brightness");
		textArgs = new TextArguments();

		BRUSH.setToggleGroup(buttonGroup);
		RUBBER.setToggleGroup(buttonGroup);
		TEXT.setToggleGroup(buttonGroup);
		BRUSH.setSelected(true);

		brush_size.getSelectionModel().select(1);

		topRow.getChildren().addAll(save);
		hbox.getChildren().addAll(BRUSH, RUBBER, TEXT, brush_size, colorPicker);
		setTop(topRow);
		setBottom(hbox);

		actions();
	}

	public void bindLayers(ArrayList<Canvas> layers) {
		this.layers = layers;
	}

	public Toggle getSelectedTool() {
		return buttonGroup.getSelectedToggle();
	}
	
	public Color getColor()
	{
		return colorPicker.getValue();
	}

	private void actions() {
		save.setOnAction(e -> {
			if (layers.size() > 0) {
				FileChooser fc = new FileChooser();
				File file = fc.showSaveDialog(this.getScene().getWindow());

				if (file != null) {
					Canvas canvas = new Canvas(layers.get(0).getWidth(), layers.get(0).getHeight());
					GraphicsContext gc = canvas.getGraphicsContext2D();
					gc.setFill(Color.TRANSPARENT);
					gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
					for (int i = layers.size() - 1; i > -1; i--) {
						Canvas c = layers.get(i);
						SnapshotParameters sp = new SnapshotParameters();
						sp.setFill(Color.TRANSPARENT);
						WritableImage wi = c.snapshot(sp, null);
						gc.drawImage(wi, 0, 0);
					}
					SnapshotParameters parameters = new SnapshotParameters();
					parameters.setFill(Color.TRANSPARENT);
					System.out.println(parameters.getFill());
					WritableImage snapshot = canvas.snapshot(parameters, null);
					try {
						ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		TEXT.setOnAction(e -> {
			setupTextStage();
		});
		
		changeBrightness.setOnAction(e -> {
			setBrightness(0.1f);
		});
	}
	
	private void setupTextStage() {
//		TEXT.setSelected(true);
		Stage stage = new Stage();
		VBox vbox = new VBox();
		HBox fontBox = new HBox();
		Scene scene = new Scene(vbox);
		TextField tf = new TextField();
		tf.setPromptText("Enter Text");
		tf.setText(textArgs.getText());
		
		String[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		ObservableList<String> fonts = FXCollections.observableArrayList(fontList);
		
		ListView<String> listFonts = new ListView<String>();
		ListView<Number> listSizes = new ListView<Number>();
		listFonts.setItems(fonts);
		listSizes.setItems(brushSizeList);
		listFonts.getSelectionModel().select(textArgs.getFont().getName());
		listSizes.getSelectionModel().select(textArgs.getFont().getSize());
		textArgs.getFont().getName();
		
		fontBox.getChildren().addAll(listFonts, listSizes);
		vbox.getChildren().addAll(tf, fontBox);
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.show();
		
		stage.setOnCloseRequest(e -> {
			textArgs.setText(tf.getText());
			Font font = new Font(listFonts.getSelectionModel().getSelectedItem(), listSizes.getSelectionModel().getSelectedItem().doubleValue());
			textArgs.setFont(font);
		});
	}
	
	public TextArguments getTextArgs()
	{
		return textArgs;
	}

	public void setBrightness(float brightness)
	{
		ColorAdjust colorAdjust = new ColorAdjust();
		 colorAdjust.setBrightness(brightness);
		
		for(int i = 0; i < layers.size(); i++)
		{
			if(layers.get(i).isVisible())
			{
				layers.get(i).setEffect(colorAdjust);
				break;
			}
		}
	}

	public double getBurshSize() {
		Number value = brush_size.getSelectionModel().getSelectedItem();
		return value.doubleValue();
	}

}
