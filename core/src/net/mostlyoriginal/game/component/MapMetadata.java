package net.mostlyoriginal.game.component;

import com.artemis.Component;
import net.mostlyoriginal.game.G;

/**
 * @author Daan van Yperen
 */
public class MapMetadata extends Component {
	public float unitsPerPixel = G.UNITS_PER_PIXEL;

	public void set(MapMetadata metadata) {
		this.unitsPerPixel = metadata.unitsPerPixel;
	}
}
