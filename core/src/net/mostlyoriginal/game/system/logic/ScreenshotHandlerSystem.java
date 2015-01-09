package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.api.ScreenshotHelper;
import net.mostlyoriginal.game.component.ui.ButtonListener;
import net.mostlyoriginal.game.component.ui.Transient;
import net.mostlyoriginal.game.manager.EntityFactoryManager;
import net.mostlyoriginal.game.manager.LayerLoaderSystem;
import net.mostlyoriginal.game.manager.LayerManager;

/**
 * @author Daan van Yperen
 */
@Wire
public class ScreenshotHandlerSystem extends EntitySystem {

	EntityFactoryManager entityFactoryManager;
	private int counter;
	private LayerManager layerManager;
	public boolean screenshot = false;
	private FrameBuffer fbo;
	private LayerLoaderSystem layerLoaderSystem;

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
				screenshot = true;
			}

			@Override
			public boolean enabled() {
				return true;
			}
		}, 50);
	}

	public void afterRender() {
		if (screenshot && fbo != null ) {
			screenshot = false;
			int counter=0;
			FileHandle local = Gdx.files.local(layerLoaderSystem.mapName + counter++ + ".png");

			new ScreenshotHelper().screenshot(local);

			fbo.end();
			fbo.dispose();
			fbo = null;
		}

	}

	public void beforeRender() {
		if (screenshot) {
			fbo = new FrameBuffer(Pixmap.Format.RGB888, G.CANVAS_WIDTH, G.CANVAS_HEIGHT, false);
			fbo.begin();
		}

	}
}
