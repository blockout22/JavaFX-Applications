package media.player;

import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controls extends HBox {

	private MediaView mv;
	private Stage fullscreenStage;
	private Button togglePlayPause, toggleMute, toggleFullscreen;
	private Slider timeSlider, bufferSlider, volumeSlider;
	private Text curTime, timeSpacer, finishTime;
	private StackPane timeStack; 

	private boolean isFullscreen = false;
	private PlayerPane content;
	private Pane parent;

	private ContextMenu menu;
	private MenuItem changeContent;
	private boolean playing = false;

	public Controls(MediaView mv) {
		this.mv = mv;
		setSpacing(4);
		fullscreenStage = new Stage();
		togglePlayPause = new Button("play");
		timeSlider = new Slider();
		bufferSlider = new Slider();
		toggleMute = new Button("mute");
		volumeSlider = new Slider();
		toggleFullscreen = new Button("[]");
		curTime = new Text("00:00");
		timeSpacer = new Text("/");
		finishTime = new Text("00:00");
		timeStack = new StackPane();

		changeContent = new MenuItem("Change Media");
		menu = new ContextMenu(changeContent);

//		bufferSlider.setDisable(false);
//		setHgrow(timeSlider, Priority.ALWAYS);
//		setHgrow(bufferSlider, Priority.ALWAYS);
		setHgrow(timeStack, Priority.ALWAYS);
		mv.setPreserveRatio(false);
//		timeSlider.setMinWidth(50);
		timeSlider.setMaxWidth(Double.MAX_VALUE);
		bufferSlider.setMaxWidth(Double.MAX_VALUE);
//		timeStack.setMaxWidth(Double.MAX_VALUE);

		mv.setOnContextMenuRequested(e -> {
			menu.show(mv, e.getScreenX(), e.getScreenY());
		});

		mv.setOnMouseClicked(e -> {
			if (menu.isShowing()) {
				menu.hide();
			} else {
				if (e.getButton() == MouseButton.PRIMARY) {
					if (!playing) {
						mv.getMediaPlayer().play();
						togglePlayPause.setText("pause");
						playing = true;
					} else {
						mv.getMediaPlayer().pause();
						togglePlayPause.setText("play");
						playing = false;
					}
				}
			}
		});

		changeContent.setOnAction(e -> {
			FileChooser fc = new FileChooser();
			File f = fc.showOpenDialog(null);

			if (f != null) {
				togglePlayPause.setText("play");
				content.setMedia(f.toURI().toString());
			}
		});

		// togglePlayPause.setOnAction(e -> {
		// mv.getMediaPlayer().play();
		// });

		timeSlider.setOnMousePressed(e -> {
			mv.getMediaPlayer().seek(Duration.seconds(timeSlider.getValue()));
		});

		timeSlider.setOnMouseDragged(e -> {
			mv.getMediaPlayer().seek(Duration.seconds(timeSlider.getValue()));
		});

		togglePlayPause.setOnAction(e -> {
			Status status = mv.getMediaPlayer().getStatus();

			if (status == Status.UNKNOWN || status == Status.HALTED) {
				return;
			} else {
				if (status == Status.PAUSED || status == Status.READY || status == Status.STOPPED) {
					mv.getMediaPlayer().play();
					togglePlayPause.setText("pause");
					playing = true;
				} else {
					mv.getMediaPlayer().pause();
					togglePlayPause.setText("play");
					playing = false;
				}
			}
		});

		toggleMute.setOnAction(e -> {
			if (mv.getMediaPlayer().isMute()) {
				toggleMute.setText("mute");
				mv.getMediaPlayer().setMute(false);
			} else {
				toggleMute.setText("unmute");
				mv.getMediaPlayer().setMute(true);
			}
		});

		volumeSlider.setOnMousePressed(e -> {
			System.out.println(volumeSlider.getValue());
			mv.getMediaPlayer().setVolume(volumeSlider.getValue() / 100);
		});

		volumeSlider.setOnMouseDragged(e -> {
			mv.getMediaPlayer().setVolume(volumeSlider.getValue() / 100);
		});

		toggleFullscreen.setOnAction(e -> {
			if (!isFullscreen) {
				fullscreenStage = new Stage();
				fullscreenStage.fullScreenProperty().addListener(new ChangeListener<Boolean>() {
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
						if (!newValue) {
							isFullscreen = false;
							parent.getChildren().add(content);
							fullscreenStage.close();
						}
					}
				});
				BorderPane pane = new BorderPane();
				Scene scene = new Scene(pane);
				pane.setCenter(content);

				fullscreenStage.setScene(scene);
				fullscreenStage.setFullScreen(true);
				isFullscreen = true;
				fullscreenStage.show();
				mv.setFitHeight(fullscreenStage.getHeight());
				mv.setFitWidth(fullscreenStage.getWidth());
			} else {
				fullscreenStage.setFullScreen(false);
				mv.setFitHeight(parent.getHeight());
				mv.setFitWidth(parent.getWidth());
			}
		});

		setupMedia();

		timeStack.getChildren().addAll(bufferSlider, timeSlider);
		getChildren().addAll(togglePlayPause, curTime, timeSpacer, finishTime, timeStack, toggleMute, volumeSlider, toggleFullscreen);
		setStyle("-fx-background-color: rgb(255,0,0,1);");
	}

	public void setupMedia() {
		mv.getMediaPlayer().setOnReady(new Runnable() {
			public void run() {
				updateValues();
			}
		});

		mv.getMediaPlayer().onReadyProperty().addListener(new ChangeListener<Runnable>() {
			public void changed(ObservableValue<? extends Runnable> observable, Runnable oldValue, Runnable newValue) {
				System.out.println(newValue);
			}
		});

		mv.getMediaPlayer().currentTimeProperty().addListener(new ChangeListener<Duration>() {
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
				timeSlider.setValue(newValue.toSeconds());
				curTime.setText(toTime(newValue.toMillis()));
			}
		});

		mv.getMediaPlayer().statusProperty().addListener(new ChangeListener<Status>() {
			public void changed(ObservableValue<? extends Status> observable, Status oldValue, Status newValue) {
				System.out.println("STATUS: " + newValue);
			}
		});
		
		mv.getMediaPlayer().bufferProgressTimeProperty().addListener(new ChangeListener<Duration>() {
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
				System.out.println(newValue);
				bufferSlider.setValue(newValue.toSeconds());
			}
		});
		
		mv.getMediaPlayer().errorProperty().addListener(new ChangeListener<MediaException>() {
			public void changed(ObservableValue<? extends MediaException> observable, MediaException oldValue, MediaException newValue) {
				System.out.println(newValue);
			}
		});
	}

	public void updateValues() {
		timeSlider.setMax(mv.getMediaPlayer().getTotalDuration().toSeconds());
		bufferSlider.setMax(mv.getMediaPlayer().getTotalDuration().toSeconds());
		volumeSlider.setValue(mv.getMediaPlayer().getVolume() * 100);
		content = (PlayerPane) mv.getParent();
		parent = (Pane) content.getParent();
		toTime(mv.getMediaPlayer().getTotalDuration().toMillis());
		finishTime.setText(toTime(mv.getMediaPlayer().getTotalDuration().toMillis()));
	}

	private String toTime(double milliseconds) {
		int seconds = (int) (milliseconds / 1000) % 60;
		int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
		int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

		String secondsString = "" + seconds;
		String minutesString = "" + minutes;
		String hoursString = "" + hours;

		if (seconds < 10) {
			secondsString = "0" + seconds;
		}

		if (minutes < 10) {
			minutesString = "0" + minutes;
		}

		if (hours < 10) {
			hoursString = "0" + hours;
		}

		if (hours < 1) {
			return minutesString + ":" + secondsString;
		} else {
			return hoursString + ":" + minutesString + ":" + secondsString;
		}
	}
}