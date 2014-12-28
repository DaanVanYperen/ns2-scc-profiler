package net.mostlyoriginal.game.manager;

import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import net.mostlyoriginal.api.AStar;
import net.mostlyoriginal.api.AStarMap;
import net.mostlyoriginal.game.G;

/**
 * @author Daan van Yperen
 */
@Wire
public class MapManager extends Manager {

	protected static final int PATHING_CELL_SIZE = 4;
	public static final int MAP_HEIGHT = G.CANVAS_HEIGHT / PATHING_CELL_SIZE;
	public static final int MAP_WIDTH = G.CANVAS_WIDTH / PATHING_CELL_SIZE;

	protected class Map implements AStarMap {

		public Map() {
			pix = new Pixmap(MAP_WIDTH, MAP_HEIGHT, Pixmap.Format.RGBA8888);
		}

		protected Pixmap pix;

		@Override
		public boolean isBlocked(int x, int y) {

			if ( x < 0 || y < 0 || x >= pix.getWidth() || y >= pix.getHeight() ) {
				return true;
			}

			final int y1 = pix.getHeight() - y;
			final int color = pix.getPixel(x, y1);

			return ((color & 0x000000ff)) / 255f <= 0.5f;
		}
	}

	public Map map;
	public Texture mapTexture;
	private EntityFactoryManager entityFactoryManager;

	@Override
	protected void initialize() {
		super.initialize();

		map = new Map();
		byPixmap(new Pixmap(Gdx.files.internal("ns2_caged.tga")));
		mapTexture = new Texture(map.pix);
	}

	/** Generate route based on pixmap. */
	private void byPixmap(Pixmap pixmap) {

		float aspectRatio = pixmap.getWidth() / pixmap.getHeight();

		// bind pixmap to window.
		Rectangle dest = new Rectangle(0, 0, pixmap.getWidth(), pixmap.getHeight()).fitInside(new Rectangle(0, 0, G.CANVAS_WIDTH / PATHING_CELL_SIZE, G.CANVAS_HEIGHT / PATHING_CELL_SIZE));

		map.pix.drawPixmap(pixmap,
				0, 0, pixmap.getWidth(), pixmap.getHeight(),
				0, 0, (int) dest.width, (int) dest.height);

		addNode(100, 170);
		addTechpoint(100, 200);

		addNode(230, 180);
		addNode(420, 140);
		addTechpoint(465, 125);

		addNode(760, 420);
		addTechpoint(760, 450);

		map.pix.setColor(Color.RED);
		AStar.Node node = new AStar().findRoute(map, 100 / PATHING_CELL_SIZE, 200 / PATHING_CELL_SIZE, 760 / PATHING_CELL_SIZE, 420 / PATHING_CELL_SIZE, MAP_WIDTH * MAP_HEIGHT);

		while( node != null )
		{
			map.pix.drawPixel(node.x, map.pix.getHeight() - node.y);
			node = node.parent;
		}
	}

	private void addNode(int x, int y) {
		entityFactoryManager.createEntity("resourceNode", x, y, null);
	}

	private void addTechpoint(int x, int y) {
		entityFactoryManager.createEntity("techpoint", x, y, null);
	}
}
