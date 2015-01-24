package net.mostlyoriginal.game.system.logic.analysis;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.game.api.DelayedEntitySystem;
import net.mostlyoriginal.game.api.pathfinding.GridGraph;
import net.mostlyoriginal.game.api.pathfinding.GridNode;
import net.mostlyoriginal.game.component.Layer;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.component.ui.RenderMask;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.manager.NavigationGridManager;

import java.util.LinkedList;

/**
 * @author Daan van Yperen
 */
@Wire(injectInherited = true)
public class NavigationGridCalculationSystem extends DelayedEntitySystem {

	NavigationGridManager navigationGridManager;
	private LayerManager layerManager;

	private Color tmpCol = new Color();

	public NavigationGridCalculationSystem() {
		super(Aspect.getEmpty());
	}

	Color tmpColor = new Color();

	public static final Color DUCT_COLOR = new Color(236/256f,153/256f,25/256f,1f);
	public static final Color FLOOR_COLOR = new Color(112/256f,112/256f,112/256f,1f);

	@Override
	protected long maxDuration() {
		return 4;
	}

	public boolean similar(Color c1, Color c2, float tolerance)
	{
		return Math.abs(c1.r - c2.r) < tolerance &&
				Math.abs(c1.g - c2.g) < tolerance &&
				Math.abs(c1.b - c2.b) < tolerance;
	}

	private boolean isWalkable(int rawColor, Team team) {

		tmpColor.set(rawColor);

		// only aliens can walk over duct colors.
		if ( similar(tmpColor, DUCT_COLOR,0.25f) ) {
			return team == Team.ALIEN;
		}

		if ( similar(tmpColor, Color.WHITE,0.10f) ) {
			return false;
		}

		return tmpColor.a >= 0.5f;
	}

	@Override
	protected void collectJobs(ImmutableBag<Entity> entities, LinkedList<Job> jobs) {

		for (Team team : Team.values()) {

			Layer navMask = layerManager.getTeamNavLayer(team);
			navMask.clear();

			GridGraph graph = new GridGraph(NavigationGridManager.GRID_WIDTH, NavigationGridManager.GRID_HEIGHT);
			navigationGridManager.setNavigationGrid(team, graph);

			jobs.add(new RefreshNavigationGrid(team, navMask, graph));
		}
	}

	private class RefreshNavigationGrid implements Job {
		private int x;
		private Team team;
		private final Layer navMask;
		private final Layer rawMapLayer;
		private final GridGraph graph;
		private int index;

		public RefreshNavigationGrid(Team team, Layer navMask, GridGraph graph) {
			this.team = team;
			this.navMask = navMask;
			this.rawMapLayer = layerManager.getLayer("RAW", RenderMask.Mask.BASIC);
			this.graph = graph;

			this.x = 0;
			this.index=0;
		}

		public void run() {

			if ( x < NavigationGridManager.GRID_WIDTH ) {
				for (int y = 0; y < NavigationGridManager.GRID_HEIGHT; y++) {
					boolean isWalkable;
					int rawColor = rawMapLayer.pixmap.getPixel(x, rawMapLayer.pixmap.getHeight() - y);
					if (x == 0 || y == 0 || x - 1 == NavigationGridManager.GRID_WIDTH || y - 1 == NavigationGridManager.GRID_HEIGHT)
						// prevent walking map borders.
						isWalkable = false;
					else {
						// blocked by map mask.
						isWalkable = isWalkable(rawColor, team);
					}

					// generate mask based on blockades.
					tmpCol.set(rawColor);
					if (isWalkable) {
						float transparency = 0.3f;
						tmpCol.r = (tmpCol.r * transparency + team.getBackgroundColor().r * (1 - transparency));
						tmpCol.g = (tmpCol.g * transparency + team.getBackgroundColor().g * (1 - transparency));
						tmpCol.b = (tmpCol.b * transparency + team.getBackgroundColor().b * (1 - transparency));
						tmpCol.a = (tmpCol.a * transparency + team.getBackgroundColor().a * (1 - transparency));
						navMask.drawPixel(x, navMask.pixmap.getHeight() - y, tmpCol);
					}

					graph.set(x, y, isWalkable ? new GridNode(graph, x, y, index++) : null);
				}

				x++;

				if ( isCompleted() )
				{
					graph.bakeNeighbours();
				}
			}
		}

		@Override
		public boolean isCompleted() {
			return x == NavigationGridManager.GRID_WIDTH;
		}
	}
}