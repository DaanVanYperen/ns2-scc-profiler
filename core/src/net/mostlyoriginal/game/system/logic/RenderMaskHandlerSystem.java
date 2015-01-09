package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.game.component.ui.ButtonListener;
import net.mostlyoriginal.game.component.ui.RenderMask;
import net.mostlyoriginal.game.manager.EntityFactoryManager;

/**
 * Set renderables visible or invisible based on active masks.
 *
 * @author Daan van Yperen
 */
@Wire
public class RenderMaskHandlerSystem extends EntityProcessingSystem {

	protected ComponentMapper<RenderMask> mRenderMask;
	protected ComponentMapper<Invisible> mInvisible;

	protected RenderMask.Mask activeMask = RenderMask.Mask.TEAM_DOMAINS;
	private EntityFactoryManager entityFactoryManager;

	@SuppressWarnings("unchecked")
	public RenderMaskHandlerSystem() {
		super(Aspect.getAspectForAll(RenderMask.class));
	}

	@Override
	protected void initialize() {
		super.initialize();


		// row 1
		createLayerButton(50 + 40 * 11, "layer-1", RenderMask.Mask.BASIC, 50);
		createLayerButton(50 + 40 * 12, "layer-2", RenderMask.Mask.PATHFIND_MARINE, 50);
		createLayerButton(50 + 40 * 13, "layer-3", RenderMask.Mask.PATHFIND_ALIEN, 50);
		createLayerButton(50 + 40 * 14, "layer-4", RenderMask.Mask.RT_SYMMETRY_ALIEN, 50);
		createLayerButton(50 + 40 * 15, "layer-5", RenderMask.Mask.RT_SYMMETRY_MARINE, 50);
		createLayerButton(50 + 40 * 16, "layer-6", RenderMask.Mask.RT_PRESSURE, 50);

		// row 2
		createLayerButton(50 + 40 * 11, "layer-7", RenderMask.Mask.TEAM_DOMAINS, 50 - 40);
	}

	private void createLayerButton(int x, String id, final RenderMask.Mask mask, int y) {
		entityFactoryManager.createBasicButton(id, x, new ButtonListener() {
			@Override
			public void run() {
				activeMask = mask;
			}

			@Override
			public boolean enabled() {
				return true;
			}
		}, y);
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

	public RenderMask.Mask getActiveMask() {
		return activeMask;
	}
}
