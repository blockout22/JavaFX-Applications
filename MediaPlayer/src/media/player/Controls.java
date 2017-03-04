package media.player;

import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
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

	private String play_icon = "\u25B6";
	private String pause_icon = "\u23F8";
	private String unmute_icon = "\uD83D\uDD07";
	private String mute_icon = "\uD83D\uDD08";
	private String fullscreen_icon = "\u26F6";
	private String PLAYER_BAR_STYLE = "-fx-padding: 3px 0px 3px 0px; -fx-background-color: linear-gradient(from 25% 100% to 25% 100%, #58595b, #000000)";

	private MediaView preview;
	private Popup pop = new Popup();

	public Controls(MediaView mv) {
		this.mv = mv;
		setSpacing(4);
		setStyle(PLAYER_BAR_STYLE);
		fullscreenStage = new Stage();
		togglePlayPause = new Button(play_icon);
		timeSlider = new Slider();
		bufferSlider = new Slider();
		toggleMute = new Button(mute_icon);
		volumeSlider = new Slider();
		toggleFullscreen = new Button(fullscreen_icon);
		curTime = new Text("00:00");
		timeSpacer = new Text("/");
		finishTime = new Text("00:00");
		timeStack = new StackPane();

		changeContent = new MenuItem("Change Media");
		menu = new ContextMenu(changeContent);

		// bufferSlider.setDisable(false);
		// setHgrow(timeSlider, Priority.ALWAYS);
		// setHgrow(bufferSlider, Priority.ALWAYS);
		setHgrow(timeStack, Priority.ALWAYS);
		mv.setPreserveRatio(false);
		// timeSlider.setMinWidth(50);
		timeSlider.setMaxWidth(Double.MAX_VALUE);
		bufferSlider.setMaxWidth(Double.MAX_VALUE);
		// timeStack.setMaxWidth(Double.MAX_VALUE);

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
						togglePlayPause.setText(pause_icon);
						playing = true;
					} else {
						mv.getMediaPlayer().pause();
						togglePlayPause.setText(play_icon);
						playing = false;
					}
				}
			}
		});

		changeContent.setOnAction(e -> {
			FileChooser fc = new FileChooser();
			File f = fc.showOpenDialog(null);

			if (f != null) {
				togglePlayPause.setText(play_icon);
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
		
		timeSlider.setOnMouseEntered(e -> {
			pop.show(this.getScene().getWindow());
			pop.setX(e.getScreenX());
			pop.setY(e.getScreenY() - 100);
		});

		timeSlider.setOnMouseMoved(e -> {
			double total = mv.getMediaPlayer().getMedia().getDuration().toSeconds();
			Bounds b = timeSlider.localToScene(timeSlider.getBoundsInLocal());
			double pos = e.getSceneX();
			double perc = pos / (b.getMaxX()) * 100;
			double seekTime = preview.getMediaPlayer().getMedia().getDuration().toSeconds() / perc;
			System.out.println(b.getMinX() + " : " + b.getMaxX() + " : " + (perc) + " : " + seekTime);
			
		});

		timeSlider.setOnMouseExited(e -> {
			pop.hide();
		});

		togglePlayPause.setOnAction(e -> {
			Status status = mv.getMediaPlayer().getStatus();

			if (status == Status.UNKNOWN || status == Status.HALTED) {
				return;
			} else {
				if (status == Status.PAUSED || status == Status.READY || status == Status.STOPPED) {
					mv.getMediaPlayer().play();
					togglePlayPause.setText(pause_icon);
					playing = true;
				} else {
					mv.getMediaPlayer().pause();
					togglePlayPause.setText(play_icon);
					playing = false;
				}
			}
		});

		toggleMute.setOnAction(e -> {
			if (mv.getMediaPlayer().isMute()) {
				toggleMute.setText(mute_icon);
				mv.getMediaPlayer().setMute(false);
			} else {
				toggleMute.setText(unmute_icon);
				mv.getMediaPlayer().setMute(true);
			}
		});

		volumeSlider.setOnMousePressed(e -> {
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
		// setStyle("-fx-background-color: rgb(255,0,0,1);");
	}

	public void setupMedia() {
		preview = new MediaView(new MediaPlayer(mv.getMediaPlayer().getMedia()));
		pop.getContent().add(preview);
		preview.setFitHeight(100);
		preview.setFitHeight(100);
		mv.getMediaPlayer().setOnReady(new Runnable() {
			public void run() {
				updateValues();
			}
		});

		mv.getMediaPlayer().onReadyProperty().addListener(new ChangeListener<Runnable>() {
			public void changed(ObservableValue<? extends Runnable> observable, Runnable oldValue, Runnable newValue) {
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
			}
		});

		mv.getMediaPlayer().bufferProgressTimeProperty().addListener(new ChangeListener<Duration>() {
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
				bufferSlider.setValue(newValue.toSeconds());
			}
		});

		mv.getMediaPlayer().errorProperty().addListener(new ChangeListener<MediaException>() {
			public void changed(ObservableValue<? extends MediaException> observable, MediaException oldValue, MediaException newValue) {
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
