package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import net.mostlyoriginal.game.api.ScreenshotHelper;
import net.mostlyoriginal.game.component.ui.ButtonListener;
import net.mostlyoriginal.game.component.ui.Transient;
import net.mostlyoriginal.game.manager.EntityFactoryManager;
import net.mostlyoriginal.game.system.LayerLoaderSystem;
import net.mostlyoriginal.game.manager.LayerManager;

/**
 * @author Daan van Yperen
 */
@Wire
public class ScreenshotHandlerSystem extends EntitySystem {

	EntityFactoryManager entityFactoryManager;
	private int counter;
	private LayerManager layerManager;
	public boolean beginScreenshot = false;
	public boolean screenshotting = false;
	private LayerLoaderSystem layerLoaderSystem;
	private ScreenshotHelper screenshotHelper = new ScreenshotHelper();

	@SuppressWarnings("unchecked")
	public ScreenshotHandlerSystem() {
		super(Aspect.getAspectForAll(Transient.class));
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
	}

	@Override
	protected void initialize() {
		super.initialize();

		entityFactoryManager.createBasicButton("screenshot", 50 + 40 * 6, new ButtonListener() {
			@Override
			public void run() {
				beginScreenshot = true;
			}

			@Override
			public boolean enabled() {
				return true;
			}
		}, 50);
	}

	public void afterRender() {
		if (screenshotting) {
			screenshotting = false;
			String filename = layerLoaderSystem.mapName + counter++ + ".png";
			screenshotHelper.screenshot(filename);
		}
	}

	public void beforeRender() {
		if (beginScreenshot)  {
			beginScreenshot=false;
			screenshotting=true;
			screenshotHelper.beforeScreenshotFrame();
		}
	}
}
