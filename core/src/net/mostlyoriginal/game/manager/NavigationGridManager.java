package net.mostlyoriginal.game.manager;

import com.artemis.Manager;
import com.artemis.annotations.Wire;
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

	public static final int GRID_WIDTH = LayerManager.LAYER_WIDTH;
	public static final int GRID_HEIGHT = LayerManager.LAYER_HEIGHT;
	public static final int PATHING_CELL_SIZE = LayerManager.CELL_SIZE;

	private BlockadeSystem blockadeSystem;
	private LayerManager layerManager;

	private HashMap<Team, NavigationGrid<GridCell>> navGrid = new HashMap<>();

	/**
	 * Get raw navigation grid for team.
	 *
	 * @param team
	 * @return
	 */
	public NavigationGrid<GridCell> getNavigationGrid(Team team) {
		return navGrid.get(team);
	}

	public void setNavigationGrid( Team team, NavigationGrid<GridCell> grid )
	{
		if (!navGrid.containsKey(team)) {
			navGrid.put(team, grid);
		}
	}
}
