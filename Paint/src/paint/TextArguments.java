package paint;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TextArguments {

	private Font font;
	private String text;

	public TextArguments()
	{
		font = new Font(25);
		text = "";
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
