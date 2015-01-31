package net.mostlyoriginal.game.component;

import com.artemis.Component;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;
import net.mostlyoriginal.game.Path;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Daan van Yperen
 */
public class Routable extends Component implements IndexedNode<Routable> {
	public HashMap<Team, List<Path>> paths = new HashMap<>();

	public Routable() {
		for (Team team : Team.values()) {
			paths.put(team,new ArrayList<Path>());
		}
	}

	private boolean ignoreForPreferred = false;
	private int x;
	private int y;
	private int index;

	public void setIndex(int index) {
		this.index = index;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isIgnoreForPreferred() {
		return ignoreForPreferred;
	}

	public void setIgnoreForPreferred(boolean ignoreForPreferred) {
		this.ignoreForPreferred = ignoreForPreferred;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public Array<Connection<Routable>> getConnections() {
		throw new NotImplementedException();
	}
}
