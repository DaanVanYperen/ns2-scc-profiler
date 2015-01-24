package net.mostlyoriginal.game.api.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;

/**
* @author Daan van Yperen
*/
public class GridNodeEuclideanHeuristic implements Heuristic<GridNode> {
	@Override
	public float estimate(GridNode c1, GridNode c2) {
		return calculate(c2.x - c1.x, c2.y - c1.y);
	}

	public float calculate(float deltaX, float deltaY){
		return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}

}
