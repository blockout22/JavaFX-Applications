package internal.frame;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class InternalFrame extends Pane {
	
	private FrameRegion region;
	
	public InternalFrame()
	{
		this(300, 300, "Title");
		setTitle("Hello World");
	}
	
	public InternalFrame(double width, double height, String title)
	{
		region = new FrameRegion(width, height, title);
		getChildren().add(region);
	}
	
	public void setContent(Node node){
		region.setContent(node);
	}
	
	public void setTitle(String title){
		region.setTitle(title);
	}

}
