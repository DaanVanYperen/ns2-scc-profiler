package net.mostlyoriginal.game.api.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;

/**
 * @author Daan van Yperen
 */
public class GridNode implements IndexedNode<GridNode> {

	public Array<Connection<GridNode>> neighbours;
	private GridGraph graph;
	public final int x;
	public final int y;
	private int index;

	int xOff[] = {-1, 0, 1, 1, 1, 0, -1, -1};
	int yOff[] = {-1, -1, -1, 0, 1, 1, 1, 0};

	public GridNode(GridGraph graph, int x, int y, int index) {
		this.graph = graph;
		this.x = x;
		this.y = y;
		this.index = index;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public Array<Connection<GridNode>> getConnections() {

		if (neighbours == null) {
			neighbours = new Array<>(8);

			for (int dir = 0; dir <= 7; dir++) {
				final int xx = x + xOff[dir];
				final int yy = y + yOff[dir];

				if (xx >= 0 &&
						yy >= 0 &&
						xx < graph.width &&
						yy < graph.height && graph.nodes[yy][xx] != null)
					neighbours.add(new GridConnection<GridNode>(this, graph.nodes[yy][xx],
							(xOff[dir] == 0 || yOff[dir] == 0 ? 1f : 1.414f) * 10f));
			}
		}

		return neighbours;
	}
}
