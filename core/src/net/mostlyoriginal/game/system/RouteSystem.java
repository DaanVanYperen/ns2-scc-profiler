package net.mostlyoriginal.game.system;

import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.G;

/**
 * @author Daan van Yperen
 */
@Wire
public class RouteSystem extends VoidEntitySystem {

	private SpriteBatch spriteBatch;
	private Texture mapTexture;

	private CameraSystem cameraSystem;
	private Pixmap map;

	@Override
	protected void initialize() {
		super.initialize();
		spriteBatch = new SpriteBatch();

		map = new Pixmap(G.CANVAS_WIDTH, G.CANVAS_HEIGHT, Pixmap.Format.RGBA8888);

		byPixmap(new Pixmap(Gdx.files.internal("ns2_caged.tga")));

		mapTexture = new Texture(map);
	}

	/** Generate route based on pixmap. */
	private void byPixmap(Pixmap pixmap) {

		float aspectRatio = pixmap.getWidth() / pixmap.getHeight();

		// bind pixmap to window.
		Rectangle dest = new Rectangle(0, 0, pixmap.getWidth(), pixmap.getHeight()).fitInside(new Rectangle(0, 0, G.CANVAS_WIDTH, G.CANVAS_HEIGHT));

		map.drawPixmap(pixmap,
				0,0,pixmap.getWidth(), pixmap.getHeight(),
				0,0,(int)dest.width, (int)dest.height);
	}


	@Override
	protected void processSystem() {
		spriteBatch.setProjectionMatrix(cameraSystem.camera.combined);
		spriteBatch.begin();
		spriteBatch.draw(mapTexture, 0, 0, G.CANVAS_WIDTH, G.CANVAS_HEIGHT);
		spriteBatch.end();
	}
}
