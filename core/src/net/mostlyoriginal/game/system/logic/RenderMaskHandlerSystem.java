package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.game.component.ui.RenderMask;

/**
 * Set renderables visible or invisible based on active masks.
 *
 * @author Daan van Yperen
 */
@Wire
public class RenderMaskHandlerSystem extends EntityProcessingSystem {

	protected ComponentMapper<RenderMask> mRenderMask;
	protected ComponentMapper<Invisible> mInvisible;

	protected RenderMask.Mask activeMask = RenderMask.Mask.PATHFIND_ALIEN;

	public RenderMaskHandlerSystem() {
		super(Aspect.getAspectForAll(RenderMask.class));
	}

	@Override
	protected void process(Entity e) {
		RenderMask renderMask = mRenderMask.get(e);
		if (renderMask != null && renderMask.visible != null) {
			if (renderMask.visible.contains(activeMask)) {
				if (mInvisible.has(e)) {
					e.edit().remove(Invisible.class);
				}
			} else {
				if (!mInvisible.has(e)) {
					e.edit().add(new Invisible());
				}
			}
		}
	}
}
