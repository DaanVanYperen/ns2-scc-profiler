package net.mostlyoriginal.game.system.logic.analysis;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.ai.pfa.*;
import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.utils.Array;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.Path;
import net.mostlyoriginal.game.api.DelayedEntitySystem;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.manager.NavigationGridManager;
import net.mostlyoriginal.game.system.logic.RefreshHandlerSystem;

import java.util.LinkedList;

/**
 * @author Daan van Yperen
 */
@Wire(injectInherited = true)
public class PreferredRouteCalculationSystem extends DelayedEntitySystem {

	protected LayerManager layerManager;

	protected ComponentMapper<Pos> mPos;
	protected ComponentMapper<Routable> mRoutable;
	private NavigationGridManager navigationGridManager;
	private Team pathfindTeam;

	private RouteCalculationSystem routeCalculationSystem;
	private RefreshHandlerSystem refreshHandlerSystem;


	@SuppressWarnings("unchecked")
	public PreferredRouteCalculationSystem() {
		super(Aspect.getAspectForAll(Routable.class, Pos.class));
	}

	@Override
	protected long maxDuration() {
		return 0;
	}

	@Override
	protected void postJobs() {
		refreshHandlerSystem.purgeAllTransientEntities();
	}

	@Override
	protected void initialize() {
		setPrerequisiteSystems(routeCalculationSystem);
	}

	@Override
	protected void collectJobs(ImmutableBag<Entity> entities, LinkedList<Job> jobs) {

		final Array<Routable> routables = new Array<>(entities.size());
		for (int a = 0, size = entities.size(); a < size; a++) {
			// set index, this is required for the pathfinding logic.
			final Routable routable = mRoutable.get(entities.get(a));
			routables.add(routable);
			routable.setIndex(a);
		}

		for (Team team : Team.values()) {
			final DefaultIndexedGraph<Routable> graph = new TeamDefaultIndexedGraph(team, routables);

			for (int a = 0, size = entities.size(); a < size; a++) {

				for (int b = a + 1; b < size; b++) {
					jobs.add(new CalculatePreferredRoute(team, graph, entities.get(a), entities.get(b)));
				}
			}
		}
	}

	private class CalculatePreferredRoute implements Job {
		private Team team;
		private DefaultIndexedGraph<Routable> graph;
		private final Entity entityA;
		private final Entity entityB;
		private final IndexedAStarPathFinder<Routable> finder;
		private CalculatePreferredRoute.RoutableHeuristic heuristic = new CalculatePreferredRoute.RoutableHeuristic();

		public CalculatePreferredRoute(Team team, DefaultIndexedGraph<Routable> graph, Entity entityA, Entity entityB) {
			this.team = team;
			this.graph = graph;
			this.entityA = entityA;
			this.entityB = entityB;

			finder = new IndexedAStarPathFinder<>(graph);
		}

		@Override
		public void run() {
			final Routable routableA = mRoutable.get(entityA);
			final Routable routableB = mRoutable.get(entityB);

			// we don't care about ignored preferred nodes.
			if (routableA.isIgnoreForPreferred() || routableB.isIgnoreForPreferred()) {
				return;
			}

			final GraphPath<Connection<Routable>> path = new DefaultGraphPath<>(8);

          			if ( finder.searchConnectionPath(routableA, routableB, heuristic, path) )
			{
				for (Connection<Routable> connection : path) {
					markPreferred(team, connection.getFromNode(), connection.getToNode());
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

		private class RoutableHeuristic implements Heuristic<Routable> {
			@Override
			public float estimate(Routable node1, Routable node2) {

				if ( node1 == node2 ) return 0;

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
		}
	}

	private Routable getRoutable(Path path) {
		return path != null
				&& path.destination != null
				&& path.destination.isActive() ? mRoutable.get(path.destination.get()) : null;
	}

	private class TeamDefaultIndexedGraph extends DefaultIndexedGraph<Routable> {
		private Team team;

		public TeamDefaultIndexedGraph(Team team, Array<Routable> nodes) {
			super(nodes);
			this.team = team;
		}

		@Override
		public Array<Connection<Routable>> getConnections(final Routable fromRoutable) {

			Array<Connection<Routable>> list = new Array<>();

			// we are only interested in team routes that are not ignored as preferred routes.
			for (Path path : fromRoutable.paths.get(team)) {
				final Routable toRoutable = getRoutable(path);

				if (toRoutable != null && !toRoutable.isIgnoreForPreferred()) {
					list.add(new DefaultConnection<Routable>(fromRoutable, toRoutable) {
						@Override
						public float getCost () {

							if ( toRoutable == fromRoutable ) return 0;

							// get all neighbours for team.
							for (Path path : fromRoutable.paths.get(team)) {
								Routable routable = getRoutable(path);
								if (routable == toRoutable) {
									float l = path.getPixelLength();
									float l2 = l * 0.005f;
									return l * (l2 > 1 ? l2 : 1);
								}
							}

							return 999999;

						}
					});
				}
			}

			return list;
		}
	}
}
