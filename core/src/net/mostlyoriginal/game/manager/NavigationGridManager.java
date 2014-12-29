package net.mostlyoriginal.game.manager;

import com.artemis.Manager;
import com.artemis.annotations.Wire;
import net.mostlyoriginal.game.component.Layer;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.system.BlockadeSystem;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;

import java.util.HashMap;

/**
 * @author Daan van Yperen
 */
@Wire
public class NavigationGridManager extends Manager {

	private static final int GRID_WIDTH = LayerManager.LAYER_WIDTH;
	private static final int GRID_HEIGHT = LayerManager.LAYER_HEIGHT;
	private static final int PATHING_CELL_SIZE = LayerManager.CELL_SIZE;

	private BlockadeSystem blockadeSystem;
	private LayerManager layerManager;

	private HashMap<Team, NavigationGrid<GridCell>> navGrid = new HashMap<>();

	/**
	 * Get raw navigation grid for team.
	 *
	 * @param team
	 * @return
	 */
	public NavigationGrid<GridCell> getNavigationGrid( Team team )
	{
		if ( !navGrid.containsKey(team) )
		{
			Layer navMask = layerManager.getTeamNavLayer(team);
			final Layer rawMapLayer = layerManager.getRawLayer();


			final GridCell[][] cells = new GridCell[GRID_WIDTH][GRID_HEIGHT];
			for (int x=0;x<GRID_WIDTH;x++) {
				for (int y = 0; y < GRID_HEIGHT; y++)
				{
					boolean isWalkable;

					if ( x == 0 || y == 0 || x-1 == GRID_WIDTH || y-1 == GRID_HEIGHT )
						// prevent walking map borders.
						isWalkable = false;
					else if ( blockadeSystem.blockaded(x * PATHING_CELL_SIZE, y * PATHING_CELL_SIZE, team ) )
						// blocked by team blockades.
						isWalkable = false;
					else {
						// blocked by map mask.
						int color = rawMapLayer.pixmap.getPixel(x, rawMapLayer.pixmap.getHeight() - y);
						isWalkable = ((color & 0x000000ff)) / 255f >= 0.5f;
					}

					// generate mask based on blockades.
					if ( isWalkable )
					{
						navMask.drawPixel(x, navMask.pixmap.getHeight() - y,
								team.getBackgroundColor());
					}

					cells[x][y] = new GridCell(x,y, isWalkable);
				}
			}
			navGrid.put(team, new NavigationGrid<GridCell>(cells));
		}

		return navGrid.get(team);
	}

	public void reset() {
		navGrid.clear();
		for (Team team : Team.values()) {
			layerManager.getTeamNavLayer(team).clear();
		}
	}
}
