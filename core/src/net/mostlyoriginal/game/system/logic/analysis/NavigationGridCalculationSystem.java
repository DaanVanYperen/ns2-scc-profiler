package net.mostlyoriginal.game.system.logic.analysis;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.game.api.DelayedEntitySystem;
import net.mostlyoriginal.game.component.Layer;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.component.ui.RenderMask;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.manager.NavigationGridManager;
import net.mostlyoriginal.game.system.BlockadeSystem;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;

import java.util.LinkedList;

/**
 * @author Daan van Yperen
 */
@Wire(injectInherited = true)
public class NavigationGridCalculationSystem extends DelayedEntitySystem {

	NavigationGridManager navigationGridManager;
	private BlockadeSystem blockadeSystem;
	private LayerManager layerManager;

	private Color tmpCol = new Color();

	public NavigationGridCalculationSystem() {
		super(Aspect.getEmpty());
	}

	Color tmpColor = new Color();

	@Override
	protected long maxDuration() {
		return 8;
	}

	private boolean isWalkable(int rawColor) {
		tmpColor.set(rawColor);
		return tmpColor.a >= 0.5f && (tmpColor.r < 1f || tmpColor.g < 1f || tmpColor.b < 1f);
	}

	@Override
	protected void collectJobs(ImmutableBag<Entity> entities, LinkedList<Job> jobs) {

		for (Team team : Team.values()) {

			layerManager.getTeamNavLayer(team).clear();
			Layer navMask = layerManager.getTeamNavLayer(team);

			NavigationGrid<GridCell> grid = new NavigationGrid<GridCell>(new GridCell[NavigationGridManager.GRID_WIDTH][NavigationGridManager.GRID_HEIGHT]);
			navigationGridManager.setNavigationGrid(team, grid);

			jobs.add(new RefreshNavigationGrid(team, navMask, grid));
		}
	}

	private class RefreshNavigationGrid implements Job {
		private int x;
		private Team team;
		private final Layer navMask;
		private final Layer rawMapLayer;
		private final NavigationGrid<GridCell> grid;

		public RefreshNavigationGrid(Team team, Layer navMask, NavigationGrid<GridCell> grid) {
			this.team = team;
			this.navMask = navMask;
			this.rawMapLayer = layerManager.getLayer("RAW", RenderMask.Mask.BASIC);
			this.grid = grid;

			this.x = 0;
		}

		public void run() {

			int remainingCycles = 50;
			while ( remainingCycles-- > 0 && x < NavigationGridManager.GRID_WIDTH ) {
				for (int y = 0; y < NavigationGridManager.GRID_HEIGHT; y++) {
					boolean isWalkable;
					int rawColor = rawMapLayer.pixmap.getPixel(x, rawMapLayer.pixmap.getHeight() - y);
					if (x == 0 || y == 0 || x - 1 == NavigationGridManager.GRID_WIDTH || y - 1 == NavigationGridManager.GRID_HEIGHT)
						// prevent walking map borders.
						isWalkable = false;
					else if (blockadeSystem.blockaded(x * NavigationGridManager.PATHING_CELL_SIZE, y * NavigationGridManager.PATHING_CELL_SIZE, team))
						// blocked by team blockades.
						isWalkable = false;
					else {
						// blocked by map mask.
						isWalkable = isWalkable(rawColor);
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
					grid.setCell(x, y, new GridCell(x, y, isWalkable));
				}

				x++;
			}
		}

		@Override
		public boolean isCompleted() {
			return x == NavigationGridManager.GRID_WIDTH;
		}
	}
}