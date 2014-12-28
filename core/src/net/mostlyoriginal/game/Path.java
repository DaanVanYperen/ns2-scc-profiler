package net.mostlyoriginal.game;

import com.artemis.Entity;
import net.mostlyoriginal.api.utils.reference.SafeEntityReference;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.LinkedList;

/**
 * @author Daan van Yperen
 */
public class Path {

	public SafeEntityReference target;
	public LinkedList<GridCell> cells;
	public final boolean reverse;

	public Path(SafeEntityReference target, LinkedList<GridCell> cells, boolean reverse) {
		this.target = target;
		this.cells = cells;
		this.reverse = reverse;
	}
}
