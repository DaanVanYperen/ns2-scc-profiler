package net.mostlyoriginal.game.component;

import com.artemis.Component;

import java.util.EnumSet;

/**
 * @author Daan van Yperen
 */
public class Blockade extends Component {
	public EnumSet<Team> passableBy = EnumSet.of(Team.ALIEN);
}
