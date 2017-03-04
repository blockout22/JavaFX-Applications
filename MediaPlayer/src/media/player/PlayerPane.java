package media.player;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;

public class PlayerPane extends Pane {

	private MediaView mv;
	private Controls controls;

	public PlayerPane() {
		mv = new MediaView(new MediaPlayer(new Media("http://clips.vorwaerts-gmbh.de/VfE_html5.mp4")));
		controls = new Controls(mv);
		
		mv.boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
			public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
				double height = newValue.getHeight();
				controls.setTranslateY(height - controls.getHeight());
			}
		});

		mv.getMediaPlayer().statusProperty().addListener(new ChangeListener<Status>() {
			public void changed(ObservableValue<? extends Status> observable, Status oldValue, Status newValue) {
				if (newValue == Status.READY) {
					controls.setTranslateY(mv.boundsInLocalProperty().get().getHeight() - controls.getHeight());
				}
			}
		});

		widthProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				getMediaView().setFitWidth(newValue.doubleValue());
				controls.setMinWidth(newValue.doubleValue());
			}
		});

		heightProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				getMediaView().setFitHeight(newValue.doubleValue());
			}
		});

		setFocusTraversable(true);
		addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
			 if(e.getY() > getHeight() - controls.getHeight())
			 {
				 controls.setVisible(true);
			 }else{
				 controls.setVisible(false);
			 }
//			 System.out.println("HEIGHT: " + getHeight());
//			 System.out.println(e.getX() + " : " + e.getY());
		});

		System.out.println(mv.boundsInLocalProperty().getValue().getHeight());
		System.out.println(controls.getHeight());

		controls.setMinWidth(150);
		getChildren().addAll(mv, controls);
		System.out.println(mv.getFitHeight());
	}

	public void setMedia(String media) {
		System.out.println(media);
		mv.getMediaPlayer().stop();
		MediaPlayer mediaPlayer = new MediaPlayer(new Media(media));
		mv.setMediaPlayer(mediaPlayer);
		controls.setupMedia();
		controls.updateValues();
	}

	public MediaView getMediaView() {
		return mv;
	}

	public double controlsHeight() {
		return controls.getHeight();
	}
}
