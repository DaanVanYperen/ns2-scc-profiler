package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.Path;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.manager.NavigationGridManager;
import org.xguzm.pathfinding.*;
import org.xguzm.pathfinding.finders.AStarFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Daan van Yperen
 */
@Wire
public class PreferredRouteCalculationSystem extends EntitySystem {

	protected LayerManager layerManager;

	private boolean resolved = false;

	private HashMap<Team, TeamGraph> teamGraphs = new HashMap<>();

	protected ComponentMapper<Pos> mPos;
	protected ComponentMapper<Routable> mRoutable;
	private PathFinder<Routable> finder;
	private NavigationGridManager navigationGridManager;
	private Team pathfindTeam;


	public PreferredRouteCalculationSystem() {
		super(Aspect.getAspectForAll(Routable.class, Pos.class));
	}

	public final class TeamGraph implements NavigationGraph<Routable> {

		private Team team;

		public TeamGraph(Team team) {
			this.team = team;
		}

		@Override
		public List<Routable> getNeighbors(Routable node) {
			ArrayList<Routable> list = new ArrayList<Routable>();

			// get all neighbours for team.
			for (Path path : node.paths.get(team)) {
				Routable routable = getRoutable(path);
				if (routable != null) {
					list.add(routable);
				}
			}

			return list;
		}

		@Override
		public List<Routable> getNeighbors(Routable node, PathFinderOptions opt) {
			return getNeighbors(node);
		}

		@Override
		public float getMovementCost(Routable node1, Routable node2, PathFinderOptions opt) {

			// get all neighbours for team.
			for (Path path : node1.paths.get(team)) {
				Routable routable = getRoutable(path);
				if ( routable == node2 ) {
					float l = path.getLength() * 0.001f;
					return l*l*l*l*l*l;
				}
			}

			return 999999;
		}

		@Override
		public boolean isWalkable(Routable node) {
			return node.isWalkable();
		}
	}

	private Routable getRoutable(Path path) {
		return path != null && path.destination != null && path.destination.isActive() ? mRoutable.get(path.destination.get()) : null;
	}

	@Override
	protected void initialize() {
		PathFinderOptions pathFinderOptions = new PathFinderOptions() {
		};
		pathFinderOptions.heuristic = new Heuristic() {
			@Override
			public float calculate(NavigationNode from, NavigationNode to) {
				return teamGraphs.get(pathfindTeam).getMovementCost((Routable)from, (Routable)to, null);
			}
		};

		finder = new AStarFinder<>(Routable.class,pathFinderOptions);
		//new NavigationGrid();
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {

		if (!resolved) {
			resolved = true;

			for (Team team : Team.values()) {
				teamGraphs.put(team, new TeamGraph(team) );
			}

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

					Routable routableA = mRoutable.get(entityA);
					Routable routableB = mRoutable.get(entityB);

					for (Team team : Team.values()) {

						// bit of a hack to get the right distances for each team.
						pathfindTeam = team;

						List<Routable> path = finder.findPath(routableA, routableB, teamGraphs.get(team));
						if ( path != null ) {
							path.add(0, routableA);

							for (int i = 1; i < path.size(); i++) {
								markPreferred( team, path.get(i-1), path.get(i));
							}
						}
					}

				}
			}
		}
	}

	/** Mark team route from src to dst as the preferred route. */
	private void markPreferred(Team team, Routable src, Routable dst) {

		for (Path path : src.paths.get(team)) {
			Routable routable = getRoutable(path);
			if (routable == dst ) {
				path.preferred = true;
			}
		}
	}
}
