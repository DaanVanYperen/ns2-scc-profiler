package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.component.TeamMember;
import net.mostlyoriginal.game.component.ui.Clickable;

/**
 * Switch team with middle mouse button.
 *
 * @author Daan van Yperen
 */
@Wire
public class TeamChangingSystem extends EntityProcessingSystem {

	protected ComponentMapper<Clickable> mClickable;
	protected ComponentMapper<TeamMember> mTeamMember;
	protected ComponentMapper<Anim> mAnim;

	public TeamChangingSystem() {
		super(Aspect.getAspectForAll(TeamMember.class, Clickable.class, Anim.class));
	}

	@Override
	protected void process(Entity e) {
		TeamMember teamMember = mTeamMember.get(e);

		if (mClickable.get(e).state == Clickable.ClickState.CLICKED_MIDDLE) {
			if (teamMember.team == null) {
				teamMember.team = Team.MARINE;
			} else {
				switch (teamMember.team) {
					case ALIEN:
						teamMember.team = null;
						break;
					case MARINE:
						teamMember.team = Team.ALIEN;
						break;
				}
			}
		}

		Anim anim = mAnim.get(e);
		anim.id = teamMember.team != null ? teamMember.art.get(teamMember.team) : teamMember.artUnaligned;
	}
}
