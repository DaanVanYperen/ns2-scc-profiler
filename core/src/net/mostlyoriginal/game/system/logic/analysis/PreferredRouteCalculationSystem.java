package net.mostlyoriginal.game.system.logic.analysis;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.Path;
import net.mostlyoriginal.game.api.DelayedEntitySystem;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.manager.NavigationGridManager;
import net.mostlyoriginal.game.system.logic.RefreshHandlerSystem;
import org.xguzm.pathfinding.*;
import org.xguzm.pathfinding.finders.AStarFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Daan van Yperen
 */
@Wire(injectInherited = true)
public class PreferredRouteCalculationSystem extends DelayedEntitySystem {

	protected LayerManager layerManager;

	private HashMap<Team, TeamGraph> teamGraphs = new HashMap<>();

	protected ComponentMapper<Pos> mPos;
	protected ComponentMapper<Routable> mRoutable;
	private PathFinder<Routable> finder;
	private NavigationGridManager navigationGridManager;
	private Team pathfindTeam;

	private RouteCalculationSystem routeCalculationSystem;
	private RefreshHandlerSystem refreshHandlerSystem;


	public PreferredRouteCalculationSystem() {
		super(Aspect.getAspectForAll(Routable.class, Pos.class));
	}

	@Override
	protected long maxDuration() {
		return 10;
	}

	@Override
	protected void postJobs() {
		refreshHandlerSystem.purgeAllTransientEntities();
	}

	@Override
	protected void initialize() {

		setPrerequisiteSystems(routeCalculationSystem);

		PathFinderOptions pathFinderOptions = new PathFinderOptions() {
		};
		pathFinderOptions.heuristic = new Heuristic() {
			@Override
			public float calculate(NavigationNode from, NavigationNode to) {
				return teamGraphs.get(pathfindTeam).getMovementCost((Routable) from, (Routable) to, null);
			}
		};

		finder = new AStarFinder<>(Routable.class, pathFinderOptions);
	}

	@Override
	protected void collectJobs(ImmutableBag<Entity> entities, LinkedList<Job> jobs) {

		for (Team team : Team.values()) {
			teamGraphs.put(team, new TeamGraph(team));
		}

		for (int a = 0, size = entities.size(); a < size; a++) {
			for (int b = a + 1; b < size; b++) {
				jobs.add(new CalculatePreferredRoute(entities.get(a), entities.get(b)));
			}
		}
	}

	private class CalculatePreferredRoute implements Job {
		private final Entity entityA;
		private final Entity entityB;

		public CalculatePreferredRoute(Entity entityA, Entity entityB) {
			this.entityA = entityA;
			this.entityB = entityB;
		}

		@Override
		public void run() {
			final Routable routableA = mRoutable.get(entityA);
			final Routable routableB = mRoutable.get(entityB);

			// we don't care about ignored preferred nodes.
			if (routableA.isIgnoreForPreferred() || routableB.isIgnoreForPreferred())
				return;

			for (Team team : Team.values()) {

				// bit of a hack to get the right distances for each team.
				pathfindTeam = team;

				final List<Routable> path = finder.findPath(routableA, routableB, teamGraphs.get(team));
				if (path != null) {
					path.add(0, routableA);

					for (int i = 1; i < path.size(); i++) {
						markPreferred(team, path.get(i - 1), path.get(i));
					}
				}
			}
		}

		/**
		 * Mark team route from src to dst as the preferred route.
		 */
		private void markPreferred(Team team, Routable src, Routable dst) {
			for (Path path : src.paths.get(team)) {
				// get path destination
				final Routable routable = getRoutable(path);
				if (routable == dst) {
					path.preferred = true;
				}
			}
		}

		@Override
		public boolean isCompleted() {
			return true;
		}
	}

	private Routable getRoutable(Path path) {
		return path != null
				&& path.destination != null
				&& path.destination.isActive() ? mRoutable.get(path.destination.get()) : null;
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
				if (routable != null && !routable.isIgnoreForPreferred()) {
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
				if (routable == node2) {
					float l = path.getPixelLength();
					float l2 = l * 0.005f;
					return l * (l2 > 1 ? l2 : 1);
				}
			}

			return 999999;
		}

		@Override
		public boolean isWalkable(Routable node) {
			return node.isWalkable();
		}
	}

}
