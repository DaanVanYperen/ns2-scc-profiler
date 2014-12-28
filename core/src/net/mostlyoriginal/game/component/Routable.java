package net.mostlyoriginal.game.component;

import com.artemis.Component;
import net.mostlyoriginal.game.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Daan van Yperen
 */
public class Routable extends Component {
	public HashMap<Team, List<Path>> paths = new HashMap<>();

	public Routable() {
		for (Team team : Team.values()) {
			paths.put(team,new ArrayList<Path>());
		}
	}
}
