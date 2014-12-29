package net.mostlyoriginal.game.manager;

import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.Path;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.system.BlockadeSystem;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.HashMap;
import java.util.List;

/**
 * @author Daan van Yperen
 */
@Wire
public class LayerManager extends Manager {

	private BlockadeSystem blockadeSystem;

	public LayerManager.Layer rawMapLayer;
    private HashMap<Team, LayerManager.Layer> teamNavigationLayer = new HashMap<>();

	public static final int CELL_SIZE = 2;
	public static final int LAYER_HEIGHT = G.CANVAS_HEIGHT / CELL_SIZE;
	public static final int LAYER_WIDTH = G.CANVAS_WIDTH / CELL_SIZE;

	public Layer getTeamNavLayer(Team team)
	{
		Layer layer = teamNavigationLayer.get(team);
		if ( layer == null )
		{
			layer = new Layer();
			teamNavigationLayer.put(team, layer);
		}
		return layer;
	}

	/** Bitmap layer. */
	public static class Layer {

		public final Pixmap pixmap;

		public Layer() {
			pixmap = new Pixmap(LAYER_WIDTH, LAYER_HEIGHT, Pixmap.Format.RGBA8888);
		}

		public void drawPixel(int x, int y, Color color) {
			pixmap.setColor(color);
			pixmap.drawPixel(x, y);
		}

		public void drawPixmapToFit(Pixmap source) {

				float aspectRatio = source.getWidth() / source.getHeight();

				// bind pixmap to window.
				Rectangle dest = new Rectangle(0, 0, source.getWidth(), source.getHeight())
						.fitInside(new Rectangle(0, 0, pixmap.getWidth(), pixmap.getHeight()));

				pixmap.drawPixmap(source,
						0, 0, source.getWidth(), source.getHeight(),
						0, 0, (int) dest.width, (int) dest.height);
		}


		public void drawPath(Path path, Color color) {
			pixmap.setColor(color);
			final List<GridCell> cells = path.cells;
			for (int i=1; i<cells.size(); i++ )
			{
				GridCell p1 = cells.get(i-1);
				GridCell p2 = cells.get(i);
				pixmap.drawLine(
						p1.x, pixmap.getHeight() - p1.y,
						p2.x, pixmap.getHeight() - p2.y);
			}
		}

		public Texture getTexture()
		{
			if  ( texture == null ) {
				refresh();
			}
			return texture;
		}

		private Texture texture;
		public void refresh() {
			if ( texture != null ) {
				texture.dispose();
				texture = null;
			}
			texture = new Texture(pixmap);
		}
	}

}
