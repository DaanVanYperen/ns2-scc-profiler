package net.mostlyoriginal.game.system.render;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.Path;
import net.mostlyoriginal.game.component.Layer;
import net.mostlyoriginal.game.component.Persistable;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.component.buildings.ResourceNode;
import net.mostlyoriginal.game.component.buildings.Techpoint;
import net.mostlyoriginal.game.component.ui.RenderMask;
import net.mostlyoriginal.game.manager.LayerManager;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Estimate what techpoints will be able to pressure resource towers
 * most effectively.
 *
 * Since teams have different speeds it matters who owns techpoints
 * to determine who has the greatest claim to a techpoint.
 *
 *
 * @author Daan van Yperen
 */
@Wire
public class TechpointPressureSystem extends EntitySystem {

	protected LayerManager layerManager;

	public boolean dirty = true;
	protected ComponentMapper<Routable> mRoutable;
	protected ComponentMapper<Persistable> mPersistable;
	protected ComponentMapper<Techpoint> mTechpoint;
	protected ComponentMapper<RenderMask> mRenderMask;


	public TechpointPressureSystem() {
		super(Aspect.getAspectForAll(Routable.class, Pos.class, ResourceNode.class));
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {

		if (dirty) {
			dirty = false;


			for (int i = 0, s = entities.size(); i < s; i++) {
				final Entity e = entities.get(i);
				final Routable routable = mRoutable.get(e);

				plotCloseTechpoints(e, routable);
			}
		}
	}

	private void plotCloseTechpoints(Entity e, Routable routable) {

		Layer layer = layerManager.getLayer("TECHPOINTS", RenderMask.Mask.RT_SYMMETRY_ALIEN);

		List<Path> combinedPaths = new ArrayList<Path>();

		// build a list of all paths, sorted by TRAVEL TIME. which can be different based on the team running the path.
		for (Team team : Team.values()) {
			for (Path path : routable.paths.get(team)) {
				combinedPaths.add(path);
			}
		}

		// sort by ESTIMATED TRAVEL TIME
		Collections.sort(combinedPaths, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				Path p1 = (Path) o1;
				Path p2 = (Path) o2;
				return p1.team.getTravelTimeInSeconds(p1) - p2.team.getTravelTimeInSeconds(p2);
			}
		});

		float closest = -1;
		for (Path path : combinedPaths) {
			Entity destination = path.destination.get();
			if (mTechpoint.has(destination)) {

				int travelTimeInSeconds = path.team.getTravelTimeInSeconds(path);
				if (closest == -1) closest = travelTimeInSeconds;

				// abort when paths are longer than 10% of the closest techpoint.
				if (travelTimeInSeconds > closest * 1.2f)
					break;

				GridCell cell1 = path.cells.get(0);
				GridCell cell2 = path.cells.get(path.cells.size() - 1);
				layer.pixmap.setColor(path.team.getPathColor());
				if (path.team == Team.ALIEN) {
					layer.pixmap.drawLine(
							cell1.x, layer.pixmap.getHeight() - cell1.y,
							cell2.x, layer.pixmap.getHeight() - cell2.y);
				} else {
					layer.pixmap.drawLine(
							cell1.x + 1, layer.pixmap.getHeight() - cell1.y + 1,
							cell2.x + 1, layer.pixmap.getHeight() - cell2.y + 1);
				}

			}
		}
	}
}
