package net.mostlyoriginal.game.component;

import com.artemis.Component;
import net.mostlyoriginal.game.Path;

/**
 * @author Daan van Yperen
 */
public class Traveler extends Component {

	// current location of the traveler.
	public Routable location;

	// path to current destination.
	public Path path;

	// distance traveled on path. when distance traveled exceeds path length
	// the destination has been reached.
	public float distanceTraveled;

}
