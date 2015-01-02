package net.mostlyoriginal.game.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class DistanceIndicator extends Component {
	float length;
	float speed;
	public float travelTime;

	public DistanceIndicator(float length, float speed, float travelTime) {
		this.length = length;
		this.speed = speed;
		this.travelTime = travelTime;
	}
}
