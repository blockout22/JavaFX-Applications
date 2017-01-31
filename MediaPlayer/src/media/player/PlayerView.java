package media.player;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class PlayerView extends Region {

	private MediaView mv;
	private Controls controls;
	String src = "file:///C:/Users/kie/Documents/GitHub/FTPClient/ep1.mp4";

	private ChangeListener layoutListener = new ChangeListener() {
		@Override
		public void changed(ObservableValue ov, Object t, Object t1) {
			requestLayout();
		}
	};

	protected void layoutChildren() {
		final double controlsHeight = -1000;
		final double controlOffset = 15;
		mv.relocate(0, 0);
		System.out.println("layout " + mv.getMediaPlayer().getMedia().getWidth() + " : " + mv.getFitWidth());
		controls.resizeRelocate(controlOffset, mv.getFitHeight() - controlsHeight, mv.getFitWidth(), controlsHeight);
	}

	public PlayerView() {
		Media media = new Media(src);
		MediaPlayer mp = new MediaPlayer(media);
		mv = new MediaView(mp);
		controls = new Controls(mv);

		getChildren().addAll(mv, controls);
		controls.setStyle("-fx-background-color: rgb(0,0,0.0,0.0);");
	}
	
	public MediaView getMediaView()
	{
		return mv;
	}

}
