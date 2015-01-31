package net.mostlyoriginal.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.utils.reference.SafeEntityReference;
import net.mostlyoriginal.game.api.pathfinding.grid.GridNode;
import net.mostlyoriginal.game.component.Team;

import java.util.List;

/**
 * @author Daan van Yperen
 */
public class Path implements Comparable<Path> {

	public SafeEntityReference destination;
	public List<GridNode> cells;
	public Team team;
	public boolean reversed = false;
	public boolean preferred = false;

	protected static final Vector2 vTmp = new Vector2();
	public Color color = new Color();

	public Path(SafeEntityReference destination, List<GridNode> cells, Team team, boolean reversed) {
		this.destination = destination;
		this.cells = cells;
		this.team = team;
		this.reversed = reversed;
	}

	public int getPixelLength()
	{
		float length = 0;
		for (int i = 1; i < cells.size(); i++) {
			final GridNode c1 = cells.get(i-1);
			final GridNode c2 = cells.get(i);
			length += vTmp.set(c1.x, c1.y).sub(c2.x, c2.y).len();
		}

		return (int)length;
	}

	@Override
	public int compareTo(Path o) {
		return getPixelLength() - o.getPixelLength();
	}
}
