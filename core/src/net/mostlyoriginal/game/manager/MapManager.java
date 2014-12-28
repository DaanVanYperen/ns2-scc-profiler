package net.mostlyoriginal.game.manager;

import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.system.BlockadeSystem;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;

import java.util.HashMap;

/**
 * @author Daan van Yperen
 */
@Wire
public class MapManager extends Manager {

	public static final int PATHING_CELL_SIZE = 2;
	public static final int GRID_HEIGHT = G.CANVAS_HEIGHT / PATHING_CELL_SIZE;
	public static final int GRID_WIDTH = G.CANVAS_WIDTH / PATHING_CELL_SIZE;

	private BlockadeSystem blockadeSystem;

	public class Map {

		private HashMap<Team, NavigationGrid<GridCell>> navigationGrid = new HashMap<>();

		public Map() {
			pix = new Pixmap(GRID_WIDTH, GRID_HEIGHT, Pixmap.Format.RGBA8888);
		}

		public NavigationGrid<GridCell> getNavigationGrid( Team team )
		{
			if ( !navigationGrid.containsKey(team) )
			{
				final GridCell[][] cells = new GridCell[GRID_WIDTH][GRID_HEIGHT];
				for (int x=0;x<GRID_WIDTH;x++) {
					for (int y = 0; y < GRID_HEIGHT; y++)
					{
						boolean isWalkable;

						if ( x == 0 || y == 0 || x-1 == GRID_WIDTH || y-1 == GRID_HEIGHT )
							// prevent walking map borders.
							isWalkable = false;
						else if ( blockadeSystem.blockaded(x * PATHING_CELL_SIZE, y * PATHING_CELL_SIZE, team ) )
							// blocked by team blockades.
							isWalkable = false;
						else {
							// blocked by map mask.
							int color = map.pix.getPixel(x, map.pix.getHeight() - y);
							isWalkable = ((color & 0x000000ff)) / 255f >= 0.5f;
						}

						cells[x][y] = new GridCell(x,y, isWalkable);
						if (isWalkable)
						{
							drawPixel(x *  PATHING_CELL_SIZE,y *  PATHING_CELL_SIZE, Color.GREEN);
						}
					}
				}
				navigationGrid.put(team, new NavigationGrid<GridCell>(cells));
			}

			return navigationGrid.get(team);
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

		// PURIFICATION
		addNode(260, 160);
		addDuct(195, 290);
		addDuct(185, 265);
		addDuct(335, 248);

		// VENTILATION
		addNode(410, 140);
		addTechpoint(465, 125);
		addDuct(410, 181);

		// GENERATOR
		addNode(745, 465);
		addTechpoint(745, 425);

		// ROOM NORTH-WEST OF GENERATOR
		addNode(570, 600);

		// MARINE START
		addNode(160, 600);
		addTechpoint(130, 560);

		// SEWER
		addNode(120, 390);

		// LOADING
		addNode(635, 230);

		// CENTER
		addNode(405, 400);

		// MONITORING
		addNode(355, 520);
	}

	private void addDuct(int x, int y) {
		entityFactoryManager.createEntity("duct", x, y, null);
	}

	private void addNode(int x, int y) {
		entityFactoryManager.createEntity("resourceNode", x, y, null);
	}

	private void addTechpoint(int x, int y) {
		entityFactoryManager.createEntity("techpoint", x, y, null);
	}
}
