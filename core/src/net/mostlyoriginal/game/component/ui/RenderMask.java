package net.mostlyoriginal.game.component.ui;

import com.artemis.Component;

import java.util.EnumSet;

/**
 * @author Daan van Yperen
 */
public class RenderMask extends Component {

	public RenderMask() {
	}

	public RenderMask(Mask mask) {
		visible = EnumSet.of(mask);
	}

	public static enum Mask
	{
		BASIC,
		PATHFIND_ALIEN,
		PATHFIND_MARINE,
		RT_SYMMETRY_ALIEN,
		RT_SYMMETRY_MARINE,
		RT_PRESSURE,
		TEAM_DOMAINS
	}

	public EnumSet<Mask> visible = EnumSet.noneOf(Mask.class);
}
