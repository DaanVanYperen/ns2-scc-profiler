package net.mostlyoriginal.game;

import net.mostlyoriginal.api.utils.reference.SafeEntityReference;
import net.mostlyoriginal.game.component.Team;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Daan van Yperen
 */
public class Path {

	public SafeEntityReference destination;
	public List<GridCell> cells;
	public Team team;

	public Path(SafeEntityReference destination, List<GridCell> cells, Team team) {
		this.destination = destination;
		this.cells = cells;
		this.team = team;
	}
}
