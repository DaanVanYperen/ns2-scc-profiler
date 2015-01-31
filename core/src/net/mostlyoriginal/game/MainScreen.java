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
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.api.event.common.EventManager;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.api.system.mouse.MouseCursorSystem;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.api.system.render.AnimRenderSystem;
import net.mostlyoriginal.game.manager.*;
import net.mostlyoriginal.game.system.LayerLoaderSystem;
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

		RenderBatchingSystem renderBatchingSystem = new RenderBatchingSystem();
		screenshotHandlerSystem = new ScreenshotHandlerSystem();

		world = new WorldBuilder()
				.with(
						new EventManager(),
						new GroupManager(),
						new TagManager(),
						new UuidEntityManager(),
						new NavigationGridManager(),
						new LayerManager(),
						new EntityFactoryManager(),
						new FontManager(),
						new MapMetadataManager())
				.with(
						new CollisionSystem(),
						new MouseCursorSystem(),
						new MouseClickSystem(),
						new DraggableSystem(),
						new ButtonSystem(),
						new ToolSystem(),
						new DeletableSystem(),
						new LayerLoaderSystem(),

						// 2. Plotting.
						new RoutePlotSystem(),
						new TechpointSymmetrySystem(),
						new TravelerSystem(),
						new AssetSystem(),
						new CameraSystem(CAMERA_ZOOM_FACTOR),
						new RenderMaskHandlerSystem(),

						/** Rendering */

						renderBatchingSystem)
				.withPassive(
						new AnimRenderSystem(renderBatchingSystem),
						new LayerRenderSystem(renderBatchingSystem),
						new LabelRenderSystem(renderBatchingSystem))
				.with(
						new TeamChangingSystem(),
						new TechpointPressureSystem(),
						new DomainSystem(),

						new RefreshHandlerSystem(),
						new PersistHandlerSystem(),

						screenshotHandlerSystem,

						// 1. Route Calculation.
						// we run these in reverse, so dependencies will have to cycle one frame before
						// being called which will avoid hitching due to multiple systems processing at once.
						new PreferredRouteCalculationSystem(),
						new RouteCalculationSystem(),
						new NavigationGridCalculationSystem(),

						new InputSystem(),
						new MilestoneHandlerSystem()).initialize();

		new EntityBuilder(world).with(new MouseCursor(), new Pos(), new Bounds(0, 0, 11, 12), new Renderable(10000)).tag("cursor").build();
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
