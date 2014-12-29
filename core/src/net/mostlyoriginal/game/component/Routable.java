package net.mostlyoriginal.game.component;

import com.artemis.Component;
import net.mostlyoriginal.game.Path;
import org.xguzm.pathfinding.NavigationNode;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Daan van Yperen
 */
public class Routable extends Component implements NavigationNode {
	public HashMap<Team, List<Path>> paths = new HashMap<>();

	public Routable() {
		for (Team team : Team.values()) {
			paths.put(team,new ArrayList<Path>());
		}
	}

	/* for path finders*/
	private float f, g, h;
	private boolean isWalkable = true;
	private int closedOnJob, openedOnJob;
	private NavigationNode parent;

	//for BTree
	private int index;

	@Override
	public void setIndex(int index) {
		this.index = index;
	}


	@Override
	public int getIndex() {
		return index;
	}

	public boolean isWalkable() {
		return isWalkable;
	}

	public void setWalkable(boolean isWalkable) {
		this.isWalkable = isWalkable;
	}

	public float getF() {
		return f;
	}

	public void setF(float f) {
		this.f = f;
	}

	@Override
	public float getG() {
		return g;
	}

	@Override
	public void setG(float g) {
		this.g = g;
	}

	@Override
	public float getH() {
		return h;
	}

	@Override
	public void setH(float h) {
		this.h = h;
	}

	@Override
	public NavigationNode getParent() {
		return parent;
	}

	@Override
	public void setParent(NavigationNode parent) {
		this.parent = parent;
	}

	@Override
	public int getClosedOnJob() {
		return closedOnJob;
	}

	@Override
	public void setClosedOnJob(int closedOnJob) {
		this.closedOnJob = closedOnJob;
	}

	@Override
	public int getOpenedOnJob() {
		return openedOnJob;
	}

	@Override
	public void setOpenedOnJob(int openedOnJob) {
		this.openedOnJob = openedOnJob;
	}

}
