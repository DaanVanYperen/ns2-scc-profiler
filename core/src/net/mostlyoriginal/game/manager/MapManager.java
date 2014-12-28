package net.mostlyoriginal.game.manager;

import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import net.mostlyoriginal.game.G;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;

/**
 * @author Daan van Yperen
 */
@Wire
public class MapManager extends Manager {

	public static final int PATHING_CELL_SIZE = 2;
	public static final int GRID_HEIGHT = G.CANVAS_HEIGHT / PATHING_CELL_SIZE;
	public static final int GRID_WIDTH = G.CANVAS_WIDTH / PATHING_CELL_SIZE;

	public class Map {

		NavigationGrid<GridCell> navigationGrid;

		public Map() {
			pix = new Pixmap(GRID_WIDTH, GRID_HEIGHT, Pixmap.Format.RGBA8888);
		}

		public NavigationGrid<GridCell> getNavigationGrid()
		{
			if (  navigationGrid == null )
			{
				GridCell[][] gridCells = new GridCell[GRID_WIDTH][GRID_HEIGHT];
				for (int x=0;x<GRID_WIDTH;x++) {
					for (int y = 0; y < GRID_HEIGHT; y++)
					{
						int color = map.pix.getPixel(x, map.pix.getHeight() - y);
						boolean isWalkable = ((color & 0x000000ff)) / 255f >= 0.5f;

						// prevent walking map borders.
						if ( x == 0 || y == 0 || x-1 == GRID_WIDTH || y-1 == GRID_HEIGHT )
							isWalkable=false;

						gridCells[x][y] = new GridCell(x,y, isWalkable);
						if (isWalkable)
						{
							drawPixel(x *  PATHING_CELL_SIZE,y *  PATHING_CELL_SIZE, Color.GREEN);
						}
					}
				}
				navigationGrid = new NavigationGrid<GridCell>(gridCells);
			}

			return navigationGrid;
		}

		public Pixmap pix;


		public void drawPixel(int x, int y, Color color) {
			map.pix.setColor(color);
			map.pix.drawPixel(x / PATHING_CELL_SIZE, pix.getHeight() - (y / PATHING_CELL_SIZE));
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
		refreshTexture();
	}

	public void refreshTexture() {
		mapTexture = new Texture(map.pix);
	}

	/**
	 * Generate route based on pixmap.
	 */
	private void byPixmap(Pixmap pixmap) {

		float aspectRatio = pixmap.getWidth() / pixmap.getHeight();

		// bind pixmap to window.
		Rectangle dest = new Rectangle(0, 0, pixmap.getWidth(), pixmap.getHeight()).fitInside(new Rectangle(0, 0, GRID_WIDTH, GRID_HEIGHT));

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
	}

	private void addNode(int x, int y) {
		entityFactoryManager.createEntity("resourceNode", x, y, null);
	}

	private void addTechpoint(int x, int y) {
		entityFactoryManager.createEntity("techpoint", x, y, null);
	}
}
