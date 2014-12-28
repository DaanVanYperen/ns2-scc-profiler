package net.mostlyoriginal.game.manager;

import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import net.mostlyoriginal.game.G;

/**
 * @author Daan van Yperen
 */
@Wire
public class MapManager extends Manager {

	public Texture mapTexture;
	private Pixmap map;

	private EntityFactoryManager entityFactoryManager;

	@Override
	protected void initialize() {
		super.initialize();

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
				0, 0, pixmap.getWidth(), pixmap.getHeight(),
				0, 0, (int) dest.width, (int) dest.height);

		addNode(100, 170);
		addTechpoint(100, 200);

		addNode(230, 180);
		addNode(420, 140);
		addTechpoint(465, 125);

		addNode(760, 420);
		addTechpoint(760, 450);
	}

	private void addNode(int x, int y) {
		entityFactoryManager.createEntity("resourceNode", x, y, null);
	}

	private void addTechpoint(int x, int y) {
		entityFactoryManager.createEntity("techpoint", x, y, null);
	}
}
