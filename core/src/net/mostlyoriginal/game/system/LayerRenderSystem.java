package net.mostlyoriginal.game.system;

import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.manager.MapLoaderManager;

/**
 * @author Daan van Yperen
 */
@Wire
public class LayerRenderSystem extends VoidEntitySystem {

	private SpriteBatch spriteBatch;
	private CameraSystem cameraSystem;

	private LayerManager layerManager;

	@Override
	protected void initialize() {
		super.initialize();
		spriteBatch = new SpriteBatch();
	}

	@Override
	protected void processSystem() {
		spriteBatch.setProjectionMatrix(cameraSystem.camera.combined);
		spriteBatch.begin();
		//spriteBatch.draw(layerManager.rawMapLayer.getTexture(), 0, 0, G.CANVAS_WIDTH, G.CANVAS_HEIGHT);
		spriteBatch.draw(layerManager.getTeamNavLayer(Team.ALIEN).getTexture(), 0, 0, G.CANVAS_WIDTH, G.CANVAS_HEIGHT);
		spriteBatch.end();
	}
}
