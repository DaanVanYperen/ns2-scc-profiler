package net.mostlyoriginal.game;

import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.api.system.render.AnimRenderSystem;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.manager.EntityFactoryManager;
import net.mostlyoriginal.game.manager.MapManager;
import net.mostlyoriginal.game.system.BlockadeSystem;
import net.mostlyoriginal.game.system.MapRenderSystem;
import net.mostlyoriginal.game.system.RouteCalculationSystem;

/**
 * @author Daan van Yperen
 */
public class MainScreen implements Screen {

    public static final int CAMERA_ZOOM_FACTOR = 1;
    private final World world;

    public MainScreen() {

        world = new World();

        /** UTILITY - MANAGERS */

        world.setManager(new GroupManager());
        world.setManager(new TagManager());
        world.setManager(new UuidEntityManager());

        world.setManager(new EntityFactoryManager());
        world.setManager(new MapManager());

        /** UTILITY - PASSIVE */

        world.setSystem(new RouteCalculationSystem());
        world.setSystem(new AssetSystem());
        world.setSystem(new CameraSystem(CAMERA_ZOOM_FACTOR));
        world.setSystem(new BlockadeSystem());


        /** Rendering */
        world.setSystem(new MapRenderSystem());

        RenderBatchingSystem renderBatchingSystem = new RenderBatchingSystem();
        world.setSystem(renderBatchingSystem);
        world.setSystem(new AnimRenderSystem(renderBatchingSystem), false);

        world.initialize();
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(1, 1, 1, 1);
  		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // limit world delta to prevent clipping through walls.
        world.setDelta(MathUtils.clamp(delta, 0, 1 / 15f));
        world.process();
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
