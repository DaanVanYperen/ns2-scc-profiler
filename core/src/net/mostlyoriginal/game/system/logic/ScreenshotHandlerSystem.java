package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import net.mostlyoriginal.game.MainScreen;
import net.mostlyoriginal.game.component.ui.ButtonListener;
import net.mostlyoriginal.game.component.ui.Transient;
import net.mostlyoriginal.game.manager.EntityFactoryManager;
import net.mostlyoriginal.game.manager.LayerManager;

/**
 * @author Daan van Yperen
 */
@Wire
public class ScreenshotHandlerSystem extends EntitySystem {

	EntityFactoryManager entityFactoryManager;
	private int counter;
	private LayerManager layerManager;
	private MainScreen mainScreen;

	public ScreenshotHandlerSystem(MainScreen mainScreen) {
		super(Aspect.getAspectForAll(Transient.class));
		this.mainScreen = mainScreen;
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
				mainScreen.screenshot = true;
			}

			@Override
			public boolean enabled() {
				return true;
			}
		}, 50);
	}
}
