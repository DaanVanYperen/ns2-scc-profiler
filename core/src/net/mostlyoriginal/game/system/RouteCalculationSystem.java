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
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.manager.MapManager;
import org.xguzm.pathfinding.PathFinder;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;
import org.xguzm.pathfinding.grid.finders.ThetaStarGridFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Resolve routes between all nodes.
 *
 * @author Daan van Yperen
 */
@Wire
public class RouteCalculationSystem extends EntitySystem {

	protected MapManager mapManager;
	private boolean resolved = false;

	protected ComponentMapper<Pos> mPos;
	protected ComponentMapper<Routable> mRoutable;
	private PathFinder<GridCell> finder;


	public RouteCalculationSystem() {
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

			resolveForTeam(entities, map, Team.ALIEN);
			resolveForTeam(entities, map, Team.MARINE);

			for(int i=0,s=entities.size();i<s;i++)
			{
				sortRoutesShortestToLongest(entities.get(i));
			}

			for(int i=0,s=entities.size();i<s;i++)
			{
				Entity e = entities.get(i);
				Routable routable = mRoutable.get(e);
				if ( routable.paths.size() > 0 ) {
					renderPath(routable.paths.get(0));
				}
			}

			mapManager.refreshTexture();
		}
	}

	private void sortRoutesShortestToLongest(Entity e) {
		Routable routable = mRoutable.get(e);
		Collections.sort(routable.paths);
	}

	private void resolveForTeam(ImmutableBag<Entity> entities, MapManager.Map map, Team team) {
		int size = entities.size();

		for (int a = 0; a < size; a++) {
			for (int b = a+1; b < size; b++) {
				resolveRoute(map.getNavigationGrid(team), entities.get(a), entities.get(b), team);
			}
		}
	}

	private Path resolveRoute(NavigationGrid<GridCell> grid, Entity a, Entity b, Team team) {

		// offset to center on the image, and convert to pathing space.
		// @todo cleanup the space difference.
		final Pos posA = mPos.get(a);
		final Pos posB = mPos.get(b);
		final GridCell cellA = grid.getCell((int) (posA.x + 4) / mapManager.PATHING_CELL_SIZE, (int) (posA.y + 4) / mapManager.PATHING_CELL_SIZE);
		final GridCell cellB = grid.getCell((int) (posB.x + 4) / mapManager.PATHING_CELL_SIZE, (int) (posB.y + 4) / mapManager.PATHING_CELL_SIZE);

		final List<GridCell> rawPath = finder.findPath(cellA,cellB,grid);
		if (rawPath != null) {

			final LinkedList<GridCell> cells = new LinkedList<>(rawPath);
			cells.addFirst(new GridCell((int)(posA.x + 4) / mapManager.PATHING_CELL_SIZE,(int)(posA.y + 4) / mapManager.PATHING_CELL_SIZE));

			final Path toDestination = new Path(new SafeEntityReference(b), cells, team);

			ArrayList<GridCell> reversedCells = new ArrayList<GridCell>(cells);
			Collections.reverse(reversedCells);
			final Path toSource = new Path(new SafeEntityReference(a),reversedCells, team);
			mRoutable.get(a).paths.add(toDestination);
			mRoutable.get(b).paths.add(toSource);

			return toDestination;
		};

		return null;

		// eliminate duplicate routes using the following algorithm.
		// long paths get a small penalty.
		// this makes multiple small paths preferable over small paths, hopefully eliminating
		// bad routes.
	}

	private void renderPath(Path path) {
		final List<GridCell> cells = path.cells;
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
