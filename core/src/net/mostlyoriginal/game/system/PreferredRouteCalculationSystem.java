package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.manager.NavigationGridManager;
import org.xguzm.pathfinding.PathFinder;
import org.xguzm.pathfinding.finders.AStarFinder;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

/**
 * @author Daan van Yperen
 */
@Wire
public class PreferredRouteCalculationSystem extends EntitySystem {

	protected LayerManager layerManager;

	private boolean resolved = false;

	protected ComponentMapper<Pos> mPos;
	protected ComponentMapper<Routable> mRoutable;
	private PathFinder<GridCell> finder;
	private NavigationGridManager navigationGridManager;


	public PreferredRouteCalculationSystem() {
		super(Aspect.getAspectForAll(Routable.class, Pos.class));
	}

	@Override
	protected void initialize() {
		finder = new AStarFinder<>(GridCell.class, new GridFinderOptions());

		//new NavigationGrid();
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {

		if (!resolved) {
			resolved = true;

			// eliminate duplicate routes using the following algorithm.
			// long paths get a small penalty.
			// this makes multiple small paths preferable over small paths, hopefully eliminating
			// bad routes.

			// travel from each source to each destination.

			int size = entities.size();

			for (int a = 0; a < size; a++) {
				for (int b = a+1; b < size; b++) {
					final Entity entityA = entities.get(a);
					final Entity entityB = entities.get(b);

					Routable routable = mRoutable.get(entityA);
					routable.paths.get(Team.ALIEN).get(0).preferred = true;
					routable.paths.get(Team.MARINE).get(0).preferred = true;
				}
			}
		}
	}
}
