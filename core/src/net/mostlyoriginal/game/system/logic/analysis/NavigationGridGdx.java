package net.mostlyoriginal.game.system.logic.analysis;

import com.badlogic.gdx.utils.Array;
import org.xguzm.pathfinding.PathFinderOptions;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.NavigationGridGraphNode;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

/**
 * @author Daan van Yperen
 */
public class NavigationGridGdx<T extends NavigationGridGraphNode> extends NavigationGrid<T> {

	private Array myNeighbours = new Array(16);

	public NavigationGridGdx(T[][] gridCells) {
		super(gridCells);
	}

	public Array getNeighborsAsArray(GridCell node, PathFinderOptions opt) {
		GridFinderOptions options = (GridFinderOptions) opt;
		boolean allowDiagonal = options.allowDiagonal;
		boolean dontCrossCorners = options.dontCrossCorners;
		int yDir = options.isYDown ? -1 : 1;
		int x = node.getX(), y = node.getY();
		myNeighbours.clear();
		boolean s0 = false, d0 = false, s1 = false, d1 = false,
				s2 = false, d2 = false, s3 = false, d3 = false;

		// up
		if (isWalkable(x, y + yDir)) {
			myNeighbours.add(nodes[x][y + yDir]);
			s0 = true;
		}
		// right
		if (isWalkable(x + 1, y)) {
			myNeighbours.add(nodes[x + 1][y]);
			s1 = true;
		}
		// down
		if (isWalkable(x, y - yDir)) {
			myNeighbours.add(nodes[x][y - yDir]);
			s2 = true;
		}
		// left
		if (isWalkable(x - 1, y)) {
			myNeighbours.add(nodes[x - 1][y]);
			s3 = true;
		}

		if (!allowDiagonal) {
			return myNeighbours;
		}

		if (dontCrossCorners) {
			d0 = s3 && s0;
			d1 = s0 && s1;
			d2 = s1 && s2;
			d3 = s2 && s3;
		} else {
			d0 = s3 || s0;
			d1 = s0 || s1;
			d2 = s1 || s2;
			d3 = s2 || s3;
		}

		// up left
		if (d0 && this.isWalkable(x - 1, y + yDir)) {
			myNeighbours.add(nodes[x - 1][y + yDir]);
		}
		// up right
		if (d1 && this.isWalkable(x + 1, y + yDir)) {
			myNeighbours.add(nodes[x + 1][y + yDir]);
		}
		// down right
		if (d2 && this.isWalkable(x + 1, y - yDir)) {
			myNeighbours.add(nodes[x + 1][y - yDir]);
		}
		// down left
		if (d3 && this.isWalkable(x - 1, y - yDir)) {
			myNeighbours.add(nodes[x - 1][y - yDir]);
		}

		return myNeighbours;
	}
}
