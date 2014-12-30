package net.mostlyoriginal.game;

import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.api.event.common.EventManager;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.api.system.mouse.MouseCursorSystem;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.api.system.render.AnimRenderSystem;
import net.mostlyoriginal.game.manager.*;
import net.mostlyoriginal.game.system.BlockadeSystem;
import net.mostlyoriginal.game.system.PreferredRouteCalculationSystem;
import net.mostlyoriginal.game.system.RouteCalculationSystem;
import net.mostlyoriginal.game.system.interaction.ButtonSystem;
import net.mostlyoriginal.game.system.interaction.DeletableSystem;
import net.mostlyoriginal.game.system.interaction.DraggableSystem;
import net.mostlyoriginal.game.system.interaction.MouseClickSystem;
import net.mostlyoriginal.game.system.logic.*;
import net.mostlyoriginal.game.system.render.*;

/**
 * @author Daan van Yperen
 */
public class MainScreen implements Screen {

	public static final int CAMERA_ZOOM_FACTOR = 1;
	private final World world;

	public MainScreen() {

		world = new World();

		/** UTILITY - MANAGERS */

		world.setManager(new EventManager());

		world.setManager(new GroupManager());
		world.setManager(new TagManager());
		world.setManager(new UuidEntityManager());

		world.setManager(new NavigationGridManager());
		world.setManager(new LayerManager());

		world.setManager(new EntityFactoryManager());
		world.setManager(new FontManager());

		/** UTILITY - PASSIVE */

		world.setSystem(new CollisionSystem());

		world.setSystem(new MouseCursorSystem());
		world.setSystem(new MouseClickSystem());
		world.setSystem(new DraggableSystem());
		world.setSystem(new ButtonSystem());

		world.setSystem(new DeletableSystem());

		world.setSystem(new LayerLoaderSystem());

		world.setSystem(new RouteCalculationSystem());
		world.setSystem(new PreferredRouteCalculationSystem());
		world.setSystem(new RoutePlotSystem());
		world.setSystem(new TechpointSymmetrySystem());

		world.setSystem(new TravelerSystem());

		world.setSystem(new AssetSystem());
		world.setSystem(new CameraSystem(CAMERA_ZOOM_FACTOR));
		world.setSystem(new BlockadeSystem());


		world.setSystem(new RenderMaskHandlerSystem());

		/** Rendering */

		RenderBatchingSystem renderBatchingSystem = new RenderBatchingSystem();
		world.setSystem(renderBatchingSystem);
		world.setSystem(new AnimRenderSystem(renderBatchingSystem), false);
		world.setSystem(new LayerRenderSystem(renderBatchingSystem), false);
		world.setSystem(new LabelRenderSystem(renderBatchingSystem), false);

		world.setSystem(new TeamChangingSystem());
		world.setSystem(new TechpointPressureSystem());
		world.setSystem(new DomainSystem());


		world.setSystem(new RefreshHandlerSystem());
		world.setSystem(new PersistHandlerSystem());

		world.setSystem(new ScreenshotHandlerSystem(this));

		world.initialize();

		new EntityBuilder(world).with(new MouseCursor(), new Pos(), new Bounds(0, 0, 11, 12), new Anim("cursor"), new Renderable(10000)).tag("cursor").build();
	}

	public boolean screenshot = false;

	@Override
	public void render(float delta) {

		FrameBuffer fbo = null;
		if (screenshot) {
			fbo = new FrameBuffer(Pixmap.Format.RGB888, G.CANVAS_WIDTH, G.CANVAS_HEIGHT, false);
			fbo.begin();
		}

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// limit world delta to prevent clipping through walls.
		world.setDelta(MathUtils.clamp(delta, 0, 1 / 15f));
		world.process();

		if (screenshot && fbo != null ) {
			screenshot = false;
			int counter=0;
			try {
				FileHandle fh;
				do {
					fh = new FileHandle("screenshot" + counter++ + ".png");
				} while (fh.exists());
				byte[] frameBufferPixels = ScreenUtils.getFrameBufferPixels(0, 0, G.CANVAS_WIDTH, G.CANVAS_HEIGHT , true);
				Pixmap pixmap = new Pixmap(G.CANVAS_WIDTH, G.CANVAS_HEIGHT, Pixmap.Format.RGBA8888);
				pixmap.getPixels().put(frameBufferPixels);
				PixmapIO.writePNG(fh, pixmap);
				pixmap.dispose();
			} catch (Exception e) {
			}

			fbo.end();
			fbo.dispose();
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}
}
