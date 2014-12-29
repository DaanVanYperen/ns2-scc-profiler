package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.delegate.DeferredEntityProcessingSystem;
import net.mostlyoriginal.api.system.delegate.EntityProcessPrincipal;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.Layer;

/**
 * @author Daan van Yperen
 */
@Wire
public class LayerRenderSystem extends DeferredEntityProcessingSystem {

	protected ComponentMapper<Pos> mPos;
	protected ComponentMapper<Layer> mLayer;

	private SpriteBatch batch;
	private CameraSystem cameraSystem;

	public LayerRenderSystem(EntityProcessPrincipal principal) {
		super(Aspect.getAspectForAll(Pos.class, Layer.class).exclude(Invisible.class), principal);
	}

	@Override
	protected void initialize() {
		super.initialize();
		batch = new SpriteBatch(2000);
	}

	@Override
	protected void begin() {
		batch.setProjectionMatrix(cameraSystem.camera.combined);
		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);
	}

	@Override
	protected void end() {
		batch.end();
	}

	/**
	 * Pixel perfect aligning.
	 */
	private float roundToPixels(final float val) {
		// since we use camera zoom rounding to integers doesn't work properly.
		return ((int) (val * cameraSystem.zoom)) / (float) cameraSystem.zoom;
	}

	protected void process(final Entity entity) {
		final Layer layer = mLayer.get(entity);

		if (layer.visible) {
			final Pos pos = mPos.get(entity);
			final Texture texture = layer.getTexture();
			batch.draw(texture,
					roundToPixels(pos.x),
					roundToPixels(pos.y),
					G.CANVAS_WIDTH,
					G.CANVAS_HEIGHT);
		}

	}
}
