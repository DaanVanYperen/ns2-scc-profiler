package net.mostlyoriginal.game.component;

import com.artemis.Component;

import java.util.HashMap;

/**
 * @author Daan van Yperen
 */
public class TeamMember extends Component {

	public Team team;

	// art to use, based on active team.
	public HashMap<Team, String> art = new HashMap<>();
	public String artUnaligned;
}
