package net.mostlyoriginal.game.component;

import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.game.G;

/**
 * @author Daan van Yperen
 */
public enum Team {
	ALIEN(Color.GREEN, Color.RED, G.ALIEN_SPEED_FAST),
	MARINE(Color.ORANGE, Color.BLUE, G.MARINE_SPEED_FAST);

	private float fastSpeed;

	Team(Color backgroundColor, Color pathColor, float fastSpeed) {
		this.backgroundColor = backgroundColor;
		this.pathColor = pathColor;
		this.fastSpeed = fastSpeed;
	}

	private Color backgroundColor;
	private Color pathColor;

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getPathColor() {
		return pathColor;
	}

	public float getFastSpeed() {
		return fastSpeed;
	}
}
