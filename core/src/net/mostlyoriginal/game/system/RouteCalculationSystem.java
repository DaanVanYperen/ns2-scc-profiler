package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.utils.reference.SafeEntityReference;
import net.mostlyoriginal.game.Path;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.manager.NavigationGridManager;
import org.xguzm.pathfinding.PathFinder;
import org.xguzm.pathfinding.finders.AStarFinder;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

import java.util.*;

/**
 * Resolve routes between all nodes.
 *
 * @author Daan van Yperen
 */
@Wire
public class RouteCalculationSystem extends EntitySystem {

	protected LayerManager layerManager;

	private boolean resolved = false;

	protected ComponentMapper<Pos> mPos;
	protected ComponentMapper<Routable> mRoutable;
	protected ComponentMapper<Bounds> mBounds;
	private PathFinder<GridCell> finder;
	private NavigationGridManager navigationGridManager;


	public RouteCalculationSystem() {
		super(Aspect.getAspectForAll(Routable.class, Pos.class));
	}

	@Override
	protected void initialize() {
		//create a finder either using the default options
		GridFinderOptions opt = new GridFinderOptions();
		opt.dontCrossCorners=true;
		finder = new AStarFinder<>(GridCell.class, opt);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {

		if (!resolved) {
			resolved = true;

			for (Team team : Team.values()) {
				resolveForTeam(entities, team);
			}

			for(int i=0,s=entities.size();i<s;i++)
			{
				sortRoutesShortestToLongest(entities.get(i));
			}
		}
	}

	private void sortRoutesShortestToLongest(Entity e) {
		Routable routable = mRoutable.get(e);
		for (Team team : Team.values()) {
			Collections.sort(routable.paths.get(team));
		}
	}

	private void resolveForTeam(ImmutableBag<Entity> entities,Team team) {
		int size = entities.size();

		for (int a = 0; a < size; a++) {
			for (int b = a+1; b < size; b++) {
				resolveRoute(navigationGridManager.getNavigationGrid(team), entities.get(a), entities.get(b), team);
			}
		}
	}

	private Path resolveRoute(NavigationGrid<GridCell> grid, Entity a, Entity b, Team team) {

		// offset to center on the image, and convert to pathing space.
		// @todo cleanup the space difference.
		final Pos posA = mPos.get(a);
		final Pos posB = mPos.get(b);

		Bounds boundsA = mBounds.get(a);
		Bounds boundsB = mBounds.get(b);

		int aX = (int) (posA.x + boundsA.cx()) / LayerManager.CELL_SIZE;
		int aY = (int) (posA.y + boundsA.cy()) / LayerManager.CELL_SIZE;
		final GridCell cellA = grid.getCell(aX, aY);

		int bX = (int) (posB.x + boundsB.cx()) / LayerManager.CELL_SIZE;
		int bY = (int) (posB.y + boundsB.cy()) / LayerManager.CELL_SIZE;
		final GridCell cellB = grid.getCell(bX, bY);

		mRoutable.get(a).setX(aX);
		mRoutable.get(a).setY(aY);
		mRoutable.get(b).setX(bX);
		mRoutable.get(b).setY(bY);

		final List<GridCell> rawPath = finder.findPath(cellA,cellB,grid);
		if (rawPath != null) {


			final LinkedList<GridCell> cells = new LinkedList<>(rawPath);
			cells.addFirst(new GridCell(aX, aY));

			final Path toDestination = new Path(new SafeEntityReference(b), cells, team, false);

			ArrayList<GridCell> reversedCells = new ArrayList<GridCell>(cells);
			Collections.reverse(reversedCells);
			final Path toSource = new Path(new SafeEntityReference(a),reversedCells, team, true);
			mRoutable.get(a).paths.get(team).add(toDestination);
			mRoutable.get(b).paths.get(team).add(toSource);

			return toDestination;
		};

		return null;

	}

}
