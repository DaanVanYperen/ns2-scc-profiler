package net.mostlyoriginal.game.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.AStar;
import net.mostlyoriginal.api.utils.reference.SafeEntityReference;

/**
 * @author Daan van Yperen
 */
public class Route extends Component {
	public SafeEntityReference a;
	public SafeEntityReference b;

	public AStar.Node node;
}
