package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.component.basic.Pos;
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
	private PathFinder<GridCell> finder;


	public RoutableSystem() {
		super(Aspect.getAspectForAll(Routable.class));
	}

	@Override
	protected void initialize() {
		//create a finder either using the default options
		finder = new AStarFinder<>(GridCell.class, new GridFinderOptions());
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
			int x1 = (int)(posA.x + 4) / mapManager.PATHING_CELL_SIZE;
			int y1 = (int)(posA.y + 4) / mapManager.PATHING_CELL_SIZE;
			for (int i=1; i< path.size(); i++ )
			{
				GridCell p2 = path.get(i);
				map.pix.setColor(Color.RED);
				map.pix.drawLine(
						x1,map.pix.getHeight() - y1,
						p2.x,map.pix.getHeight() - p2.y);

				x1 = p2.x;
				y1 = p2.y;
			}
			System.out.println("Path found");
		} else System.out.println("Path missing");
	}


	private void resolveRoutes() {
	}

}
