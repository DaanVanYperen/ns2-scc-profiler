package net.mostlyoriginal.game.system.render;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.component.Layer;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.buildings.Techpoint;
import net.mostlyoriginal.game.component.ui.RenderMask;
import net.mostlyoriginal.game.manager.LayerManager;

/**
 * Estimate what techpoints will be able to pressure resource towers
 * most effectively.
 * <p/>
 * Since teams have different speeds it matters who owns techpoints
 * to determine who has the greatest claim to a techpoint.
 *
 * @author Daan van Yperen
 */
@Wire
public class DomainSystem extends EntitySystem {

	protected LayerManager layerManager;

	public boolean dirty = true;
	protected ComponentMapper<Routable> mRoutable;


	public DomainSystem() {
		super(Aspect.getAspectForAll(Routable.class, Pos.class, Techpoint.class));
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {

		if (dirty) {
			dirty = false;

			Layer layer = layerManager.getLayer("DOMAINS", RenderMask.Mask.TEAM_DOMAINS);
			layerManager.clearWithMap(layer, Color.WHITE, 0.3f);

			for (int i = 0, s = entities.size(); i < s; i++) {
				final Entity e = entities.get(i);
				final Routable routable = mRoutable.get(e);
			}
		}
	}

}
