package net.mostlyoriginal.game.system;

import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.manager.MapManager;

/**
 * @author Daan van Yperen
 */
@Wire
public class MapRenderSystem extends VoidEntitySystem {

	private SpriteBatch spriteBatch;
	private CameraSystem cameraSystem;

	private MapManager mapManager;

	@Override
	protected void initialize() {
		super.initialize();
		spriteBatch = new SpriteBatch();
	}

	@Override
	protected void processSystem() {
		spriteBatch.setProjectionMatrix(cameraSystem.camera.combined);
		spriteBatch.begin();
		spriteBatch.draw(mapManager.mapTexture, 0, 0, G.CANVAS_WIDTH, G.CANVAS_HEIGHT);
		spriteBatch.end();
	}
}
