package net.mostlyoriginal.game.component;

import com.artemis.Component;
import net.mostlyoriginal.game.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daan van Yperen
 */
public class Routable extends Component {
	public List<Path> paths = new ArrayList<Path>(16);
}
