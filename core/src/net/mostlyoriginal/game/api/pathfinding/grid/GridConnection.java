package net.mostlyoriginal.game.api.pathfinding.grid;

import com.badlogic.gdx.ai.pfa.Connection;

/**
 * @author Daan van Yperen
 */
public class GridConnection<T> implements Connection<GridNode> {

	private final GridNode from;
	private final GridNode to;
	private float cost;

	public GridConnection(GridNode from, GridNode to, float cost) {
		this.from = from;
		this.to = to;
		this.cost = cost;
	}

	@Override
	public float getCost() {
		return cost;
	}

	@Override
	public GridNode getFromNode() {
		return from;
	}

	@Override
	public GridNode getToNode() {
		return to;
	}
}
