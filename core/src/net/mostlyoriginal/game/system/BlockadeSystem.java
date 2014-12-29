package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.component.Blockade;
import net.mostlyoriginal.game.component.Team;

import javax.swing.text.html.parser.Entity;

/**
 * @author Daan van Yperen
 */
@Wire
public class BlockadeSystem extends EntitySystem {

	private ComponentMapper<Bounds> bm;
	private ComponentMapper<Pos> pm;
	protected ComponentMapper<Blockade> mBlockade;


	/**
	 * Creates an entity system that uses the specified aspect as a matcher
	 * against entities.
	 *
	 * @param aspect to match against entities
	 */
	public BlockadeSystem() {
		super(Aspect.getAspectForAll(Bounds.class, Pos.class, Blockade.class));
	}

	@Override
	protected void processEntities(ImmutableBag<com.artemis.Entity> entities) {

	}
	public final boolean overlaps( final com.artemis.Entity a, int x, int y)
	{
		final Bounds b1 = bm.getSafe(a);
		final Pos p1 =  pm.getSafe(a);

		if ( b1==null || p1 ==null)
			return false;

		final float minx = p1.x + b1.minx;
		final float miny = p1.y + b1.miny;
		final float maxx = p1.x + b1.maxx;
		final float maxy = p1.y + b1.maxy;

		final float bminx = x;
		final float bminy = y;
		final float bmaxx = x + 1;
		final float bmaxy = y + 1;

		return
				!(minx > bmaxx || maxx < bminx ||
						miny > bmaxy || maxy < bminy );
	}

	/** Check if screen coordinates are blockaded. */
	public boolean blockaded(int x, int y, Team team) {

		for (com.artemis.Entity entity : getActives()) {
			if (  !mBlockade.get(entity).passableBy.contains(team) && overlaps(entity, x, y) ) {
				// blockades can be passable by teams.
				return true;
			}
		}

		return false;
	}
}
