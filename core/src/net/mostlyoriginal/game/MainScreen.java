package net.mostlyoriginal.game;

import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
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
import net.mostlyoriginal.game.system.interaction.ButtonSystem;
import net.mostlyoriginal.game.system.interaction.DeletableSystem;
import net.mostlyoriginal.game.system.interaction.DraggableSystem;
import net.mostlyoriginal.game.system.interaction.MouseClickSystem;
import net.mostlyoriginal.game.system.logic.*;
import net.mostlyoriginal.game.system.logic.analysis.NavigationGridCalculationSystem;
import net.mostlyoriginal.game.system.logic.analysis.PreferredRouteCalculationSystem;
import net.mostlyoriginal.game.system.logic.analysis.RouteCalculationSystem;
import net.mostlyoriginal.game.system.render.LabelRenderSystem;
import net.mostlyoriginal.game.system.render.LayerRenderSystem;
import net.mostlyoriginal.game.system.render.layer.DomainSystem;
import net.mostlyoriginal.game.system.render.layer.RoutePlotSystem;
import net.mostlyoriginal.game.system.render.layer.TechpointPressureSystem;
import net.mostlyoriginal.game.system.render.layer.TechpointSymmetrySystem;

/**
 * @author Daan van Yperen
 */
public class MainScreen implements Screen {

	public static final int CAMERA_ZOOM_FACTOR = 1;
	private final World world;
	private final ScreenshotHandlerSystem screenshotHandlerSystem;

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

		world.setManager(new MapMetadataManager());

		/** UTILITY - PASSIVE */

		world.setSystem(new CollisionSystem());

		world.setSystem(new MouseCursorSystem());
		world.setSystem(new MouseClickSystem());
		world.setSystem(new DraggableSystem());
		world.setSystem(new ButtonSystem());

		world.setSystem(new DeletableSystem());

		world.setSystem(new LayerLoaderSystem());

		// 2. Plotting.
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

		screenshotHandlerSystem = new ScreenshotHandlerSystem();
		world.setSystem(screenshotHandlerSystem);

		// 1. Route Calculation.
		// we run these in reverse, so dependencies will have to cycle one frame before
		// being called which will avoid hitching due to multiple systems processing at once.
		world.setSystem(new PreferredRouteCalculationSystem());
		world.setSystem(new RouteCalculationSystem());
		world.setSystem(new NavigationGridCalculationSystem());

		world.setSystem(new InputSystem());
		world.setSystem(new DistanceHandlerSystem());

		world.initialize();

		new EntityBuilder(world).with(new MouseCursor(), new Pos(), new Bounds(0, 0, 11, 12), new Anim("cursor"), new Renderable(10000)).tag("cursor").build();
	}

	@Override
	public void render(float delta) {

		screenshotHandlerSystem.beforeRender();

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// limit world delta to prevent clipping through walls.
		world.setDelta(MathUtils.clamp(delta, 0, 1 / 15f));
		world.process();

		screenshotHandlerSystem.afterRender();
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
