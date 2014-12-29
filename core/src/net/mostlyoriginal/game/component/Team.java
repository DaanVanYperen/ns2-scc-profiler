package net.mostlyoriginal.game.component;

import com.badlogic.gdx.graphics.Color;

/**
 * @author Daan van Yperen
 */
public enum Team {
	ALIEN(Color.GREEN, Color.RED),
	MARINE(Color.ORANGE, Color.BLUE);

	Team(Color backgroundColor, Color pathColor) {
		this.backgroundColor = backgroundColor;
		this.pathColor = pathColor;
	}

	private Color backgroundColor;
	private Color pathColor;

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getPathColor() {
		return pathColor;
	}
}
