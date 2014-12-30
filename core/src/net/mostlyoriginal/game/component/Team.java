package net.mostlyoriginal.game.component;

import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.game.G;

/**
 * @author Daan van Yperen
 */
public enum Team {
	ALIEN(new Color(0.1f,1f,0.5f,1f), new Color(1f,0.2f,0f,1f), G.ALIEN_SPEED_AVG),
	MARINE(Color.ORANGE, new Color(0,1f,1f,1f), G.MARINE_SPEED_AVG);

	private float avgSpeed;

	Team(Color backgroundColor, Color pathColor, float avgSpeed) {
		this.backgroundColor = backgroundColor;
		this.pathColor = pathColor;
		this.avgSpeed = avgSpeed;
	}

	private Color backgroundColor;
	private Color pathColor;

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getPathColor() {
		return pathColor;
	}

	public float getAvgSpeed() {
		return avgSpeed;
	}
}
