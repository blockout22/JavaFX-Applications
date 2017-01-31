package media.player;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class PlayerPane extends Pane {
	
	private MediaView mv;
	private Controls controls;
	
	public PlayerPane()
	{
		mv = new MediaView(new MediaPlayer(new Media("http://clips.vorwaerts-gmbh.de/VfE_html5.mp4")));
		controls = new Controls(mv);
		
		mv.boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
			public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
				double height = newValue.getHeight();
				controls.setTranslateY(height - controls.getHeight());
			}
		});
		
		widthProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				controls.setMinWidth(newValue.doubleValue());
			}
		});
		getChildren().addAll(mv, controls);
	}
	
	public void setMedia(String media)
	{
		System.out.println(media);
		mv.getMediaPlayer().stop();
		MediaPlayer mediaPlayer = new MediaPlayer(new Media(media));
		mv.setMediaPlayer(mediaPlayer);
		controls.setupMedia();
		controls.updateValues();
	}
	
	public MediaView getMediaView()
	{
		return mv;
	}
	
	public double controlsHeight()
	{
		return controls.getHeight();
	}
 }

