package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.Path;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.component.TeamMember;
import net.mostlyoriginal.game.component.Traveler;
import net.mostlyoriginal.game.manager.LayerManager;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Assists travelers in their travels.
 *
 * @author Daan van Yperen
 */
@Wire
public class TravelerSystem extends EntityProcessingSystem {

	protected ComponentMapper<Traveler> mTraveler;
	protected ComponentMapper<Pos> mPos;
	protected ComponentMapper<TeamMember> mTeamMember;
	protected ComponentMapper<Routable> mRoutable;
	protected ComponentMapper<Bounds> mBounds;
	private static Vector2 vTmp = new Vector2();

	public TravelerSystem() {
		super(Aspect.getAspectForAll(Traveler.class, Pos.class));
	}

	@Override
	protected void process(Entity e) {
		Traveler traveler = mTraveler.get(e);

		if ( traveler.location != null )
		{
			TeamMember teamMember = mTeamMember.get(e);

			if ( traveler.path != null )
			{
				travelPath(traveler, teamMember);
				checkDestinationReached(traveler);
			} else {
				pickRandomDestination(traveler, teamMember);
			}

			updateLocation(e, traveler);
		}
	}

	private void updateLocation(Entity e, Traveler traveler) {
		Pos pos = mPos.get(e);
		Bounds bounds = mBounds.get(e);
		if ( traveler.path != null )
		{
			// estimate location based on path vs distance traveled.

			float distance = 0;
			float length = 0;
			List<GridCell> cells = traveler.path.cells;
			for (int i = 1; i < cells.size(); i++) {
				GridCell c1 = cells.get(i-1);
				GridCell c2 = cells.get(i);
				length += vTmp.set(c1.x, c1.y).sub(c2.x, c2.y).len();

				if ( length >= traveler.distanceTraveled )
				{
					// put at approximate location on path.
					pos.x = c1.getX() * LayerManager.CELL_SIZE - bounds.cx();
					pos.y = c1.getY() * LayerManager.CELL_SIZE - bounds.cy();
					return;
				}
			}
		} else {

			// place traveler at starting location.
			pos.x = traveler.location.getX() * LayerManager.CELL_SIZE  - bounds.cx();
			pos.y = traveler.location.getY() * LayerManager.CELL_SIZE - bounds.cy();
		}
	}

	private void pickRandomDestination(Traveler traveler, TeamMember teamMember) {
		// test traveler logic.
		traveler.distanceTraveled = 0;
		traveler.path = randomPath(traveler, teamMember.team);
	}

	private void travelPath(Traveler traveler, TeamMember teamMember) {
		traveler.distanceTraveled += world.delta * teamMember.team.getAvgSpeed();
	}

	private void checkDestinationReached(Traveler traveler) {
		if ( traveler.distanceTraveled > traveler.path.getPixelLength() )
		{
			if ( traveler.path.destination.isActive()) {
				traveler.location = mRoutable.get(traveler.path.destination.get());
			}
			traveler.path = null;
		}
	}

	private Path randomPath(Traveler traveler, Team team ) {
		List<Path> paths = traveler.location.paths.get(team);

		List<Path> valid = new ArrayList<>(paths.size());
		for (Path path : paths) {
			if ( path.preferred )
				valid.add(path);
		}

		return valid.size() > 0 ? valid.get(MathUtils.random(valid.size()-1)) : null;
	}
}
