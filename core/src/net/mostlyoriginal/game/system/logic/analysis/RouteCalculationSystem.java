package net.mostlyoriginal.game.system.logic.analysis;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.utils.reference.SafeEntityReference;
import net.mostlyoriginal.game.Path;
import net.mostlyoriginal.game.api.DelayedEntitySystem;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.manager.NavigationGridManager;
import org.xguzm.pathfinding.PathFinder;
import org.xguzm.pathfinding.finders.AStarFinder;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Resolve routes between all nodes.
 *
 * @author Daan van Yperen
 */
@Wire(injectInherited = true)
public class RouteCalculationSystem extends DelayedEntitySystem {

	protected ComponentMapper<Pos> mPos;
	protected ComponentMapper<Routable> mRoutable;
	protected ComponentMapper<Bounds> mBounds;
	private PathFinder<GridCell> finder;
	private NavigationGridManager navigationGridManager;
	private NavigationGridCalculationSystem navigationGridCalculationSystem;


	public RouteCalculationSystem() {
		super(Aspect.getAspectForAll(Routable.class, Pos.class));
	}

	@Override
	protected void initialize() {

		setPrerequisiteSystems(navigationGridCalculationSystem);

		//create a finder either using the default options
		GridFinderOptions opt = new GridFinderOptions();
		opt.dontCrossCorners=false;
		finder = new AStarFinder<>(GridCell.class, opt);
	}

	@Override
	protected void collectJobs(ImmutableBag<Entity> entities, LinkedList<Job> jobs) {

		// clear existing routes.
		for(int i=0,s=entities.size();i<s;i++)
		{
			for (Team team : Team.values()) {
				mRoutable.get(entities.get(i)).paths.get(team).clear();
			}
		}

		for (Team team : Team.values()) {
			int size = entities.size();
			for (int a = 0; a < size; a++) {
				for (int b = a+1; b < size; b++) {
					jobs.add(new resolveRouteJob(navigationGridManager.getNavigationGrid(team), entities.get(a), entities.get(b), team));
				}
			}
		}

		jobs.add(new SortRoutesJob());
	}


	/** Job to resolve route between two entities. */
	private class resolveRouteJob implements Job {

		private final NavigationGrid<GridCell> grid;
		private final Entity a;
		private final Entity b;
		private final Team team;

		public resolveRouteJob(NavigationGrid<GridCell> grid, Entity a, Entity b, Team team) {
			this.grid = grid;
			this.a = a;
			this.b = b;
			this.team = team;
		}

		@Override
		public void run() {

			if ( !a.isActive() || !b.isActive() ) return;

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
			}
		}

		@Override
		public boolean isCompleted() {
			return true;
		}
	}


	@Override
	protected long maxDuration() {
		// routes to calculate each run.
		return 5;
	}

	private class SortRoutesJob implements Job {
		@Override
		public void run() {
			ImmutableBag<Entity> actives = getActives();
			for(int i=0,s=actives.size();i<s;i++)
			{
				// sort paths by shortest to longest.
				final Routable routable = mRoutable.get(actives.get(i));
				for (Team team : Team.values()) {
					Collections.sort(routable.paths.get(team));
				}
			}
		}

		@Override
		public boolean isCompleted() {
			return true;
		}
	}
}
