package net.mostlyoriginal.game.api.pathfinding.grid;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;

/**
 * @author Daan van Yperen
 */
public class GridGraph implements IndexedGraph<GridNode> {

	protected GridNode[][] nodes;
	public int width;
	public int height;

	public GridGraph(int width, int height) {
		this.width = width;
		this.height = height;
		nodes = new GridNode[height][width];
	}

	@Override
	public int getNodeCount() {
		return nodes.length * nodes[0].length;
	}

	@Override
	public Array<Connection<GridNode>> getConnections(GridNode fromNode) {
		return fromNode.getConnections();
	}

	public GridNode get(int x, int y) {
		return nodes[y][x];
	}

	public void bakeNeighbours()
	{
	}

	public void set(int x, int y, GridNode node) {
		nodes[y][x] = node;
	}
}
