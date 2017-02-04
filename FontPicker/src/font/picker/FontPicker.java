package font.picker;

import java.awt.GraphicsEnvironment;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FontPicker {

	private Stage stage;
	private Scene scene;

	private ObservableList<String> fonts = FXCollections.observableArrayList();
	private ObservableList<ListCell<String>> cells = FXCollections.observableArrayList();
	private ListView<String> listFonts;
	private Spinner<Integer> fontSize;
	private Button select;

	private boolean isOkSelected;

	public FontPicker() {
		setupStage();

		String[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		fonts.addAll(fontList);
		listFonts = new ListView<String>();
		fontSize = new Spinner<Integer>();
		fontSize.setEditable(true);
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5000, 11, 1);
		fontSize.setValueFactory(valueFactory);
		listFonts.setItems(fonts);
		listFonts.setCellFactory(list -> {
			ListCell<String> cell = new ListCell<String>(){
				protected void updateItem(String item, boolean empty){
					super.updateItem(item, empty);
					setText(empty ? null : item);
				}
			};
			cells.add(cell);
			return cell;
		});
		select = new Button("OK");

		select.setOnAction(e -> {
			isOkSelected = true;
			hide();
		});
		
		cells.addListener(new ListChangeListener<ListCell<String>>(){
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ListCell<String>> c) {
				for(int i = 0; i < cells.size(); i++){
					cells.get(i).setStyle("-fx-font-family:" + cells.get(i).getItem() + ";");
				}
			}
		});
	}

	private void setupStage() {
		stage = new Stage();
		stage.initStyle(StageStyle.UTILITY);
		stage.initModality(Modality.APPLICATION_MODAL);
	}

	public Font show() {
		isOkSelected = false;
		VBox pane = new VBox();
		pane.getChildren().addAll(listFonts, fontSize, select);
		scene = new Scene(pane);
		stage.setScene(scene);
		stage.showAndWait();
		if (isOkSelected) {
			Font font = new Font(listFonts.getSelectionModel().getSelectedItem(), fontSize.getValue());
			return font;
		} else {
			return null;
		}
	}

	public void hide() {
		stage.close();
	}

}
