package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.utils.reference.SafeEntityReference;
import net.mostlyoriginal.game.Path;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.manager.MapManager;
import org.xguzm.pathfinding.PathFinder;
import org.xguzm.pathfinding.finders.AStarFinder;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;
import org.xguzm.pathfinding.grid.finders.JumpPointFinder;
import org.xguzm.pathfinding.grid.finders.ThetaStarGridFinder;

import java.util.LinkedList;
import java.util.List;

/**
 * Resolve routes between all nodes.
 *
 * @author Daan van Yperen
 */
@Wire
public class RoutableSystem extends EntitySystem {

	protected MapManager mapManager;
	private boolean resolved = false;

	protected ComponentMapper<Pos> mPos;
	protected ComponentMapper<Routable> mRoutable;
	private PathFinder<GridCell> finder;


	public RoutableSystem() {
		super(Aspect.getAspectForAll(Routable.class, Pos.class));
	}

	@Override
	protected void initialize() {
		//create a finder either using the default options
		finder = new ThetaStarGridFinder<>(GridCell.class, new GridFinderOptions());
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {

		if (!resolved) {
			resolved = true;
			final MapManager.Map map = mapManager.map;

			int size = entities.size();
			for (int a = 0; a < size; a++) {
				for (int b = a+1; b < size; b++) {
					resolveRoute(map, entities.get(a), entities.get(b));
				}
			}

			mapManager.refreshTexture();
		}
	}

	private void resolveRoute(MapManager.Map map, Entity a, Entity b) {

		Pos posA = mPos.get(a);
		Pos posB = mPos.get(b);

		NavigationGrid<GridCell> grid = map.getNavigationGrid();
		GridCell cellA = grid.getCell((int) (posA.x + 4) / mapManager.PATHING_CELL_SIZE, (int) (posA.y + 4) / mapManager.PATHING_CELL_SIZE);
		GridCell cellB = grid.getCell((int) (posB.x + 4) / mapManager.PATHING_CELL_SIZE, (int) (posB.y + 4) / mapManager.PATHING_CELL_SIZE);
		List<GridCell> path = ( cellA != null && cellB != null ) ? finder.findPath(
				cellA,
				cellB,
				grid) : null;

		if (path != null) {

			final LinkedList<GridCell> cells = new LinkedList<>(path);
			cells.addFirst(new GridCell((int)(posA.x + 4) / mapManager.PATHING_CELL_SIZE,(int)(posA.y + 4) / mapManager.PATHING_CELL_SIZE));

			final Path path1 = new Path(new SafeEntityReference(b), cells, false);
			mRoutable.get(a).paths.add(path1);
			mRoutable.get(b).paths.add(new Path(new SafeEntityReference(a), cells, true));

			renderPath(path1);
		};

		// eliminate duplicate routes using the following algorithm.
		// long paths get a small penalty.
		// this makes multiple small paths preferable over small paths, hopefully eliminating
		// bad routes.
	}

	private void renderPath(Path path) {
		final LinkedList<GridCell> cells = path.cells;
		for (int i=1; i<cells.size(); i++ )
		{
			GridCell p1 = cells.get(i-1);
			GridCell p2 = cells.get(i);
			mapManager.map.pix.setColor(Color.RED);
			mapManager.map.pix.drawLine(
					p1.x,mapManager.map.pix.getHeight() - p1.y,
					p2.x,mapManager.map.pix.getHeight() - p2.y);
		}
	}


	private void resolveRoutes() {
	}

}
