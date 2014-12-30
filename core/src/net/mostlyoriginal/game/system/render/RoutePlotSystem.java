package net.mostlyoriginal.game.system.render;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.game.Path;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.component.ui.Label;
import net.mostlyoriginal.game.component.ui.RenderMask;
import net.mostlyoriginal.game.component.ui.Transient;
import net.mostlyoriginal.game.manager.LayerManager;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.List;

/**
 * @author Daan van Yperen
 */
@Wire
public class RoutePlotSystem extends EntitySystem {

	protected LayerManager layerManager;

	public boolean dirty =true;
	protected ComponentMapper<Routable> mRoutable;


	public RoutePlotSystem() {
		super(Aspect.getAspectForAll(Routable.class, Pos.class));
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {

		if (dirty) {
			dirty = false;

			// render all secondary paths as shadows.
			for(int i=0,s=entities.size();i<s;i++)
			{
				final Entity e = entities.get(i);
				final Routable routable = mRoutable.get(e);

				for (Team team : Team.values()) {
					renderSecondaryPaths(routable, team);
				}
			}

			// render all primary paths on team layers.
			for(int i=0,s=entities.size();i<s;i++)
			{
				final Entity e = entities.get(i);
				final Routable routable = mRoutable.get(e);

				for (Team team : Team.values()) {
					renderPaths(routable, team);
				}
			}

			// label all distances on primary paths.
			for(int i=0,s=entities.size();i<s;i++)
			{
				final Entity e = entities.get(i);
				final Routable routable = mRoutable.get(e);

				for (Team team : Team.values()) {
					renderLabels(routable, team);
				}
			}

			for (Team team : Team.values()) {
				layerManager.getTeamNavLayer(team).refresh();
			}
		}
	}

	Color tmpCol = new Color();

	private void renderPaths(Routable routable, Team team) {
		List<Path> paths = routable.paths.get(team);
		for (Path path : paths) {
			// don't render the reverse paths.
			if (path.preferred && !path.reversed) {

				// slightly vary path color to make it easier to track.

				path.color.set(team.getPathColor());
				path.color.r = path.color.r * MathUtils.random(0.4f,1f) + MathUtils.random(0f,0.1f);
				path.color.g = path.color.g * MathUtils.random(0.4f,1f) + MathUtils.random(0f,0.1f);
				path.color.b = path.color.b * MathUtils.random(0.4f,1f) + MathUtils.random(0f,0.1f);
				path.color.a = 0.8f;

				path.color.clamp();

				layerManager.getTeamNavLayer(team).drawPath(path, path.color, true);
			}
		}
	}

	private void renderSecondaryPaths(Routable routable, Team team) {
		List<Path> paths = routable.paths.get(team);
		for (Path path : paths) {
			// don't render the reverse paths.
			if ( !path.preferred && !path.reversed) {

				// slightly vary path color to make it easier to track.

				tmpCol.set(team.getPathColor());
				tmpCol.r = 1f;
				tmpCol.g = 1f;
				tmpCol.b = 1f;
				tmpCol.a = 0.08f;

				layerManager.getTeamNavLayer(team).drawPath(path, tmpCol, false);
			}
		}
	}

	private void renderLabels(Routable routable, Team team) {
		List<Path> paths = routable.paths.get(team);
		for (Path path : paths) {
			if (path.preferred && !path.reversed) {
				addLabel(path.color,team, path);
			}
		}
	}

	private Vector2 vTmp = new Vector2();


	private void addLabel(Color lineColor, Team team, Path path) {

		int center = path.cells.size() / 2;
		GridCell cell = path.cells.get(center);

		// use a couple distance to get a smoother angle.
		GridCell cell2 = center + 3 < path.cells.size() ? path.cells.get(center+3) : cell;

		int travelTimeSeconds = team.getTravelTimeInSeconds(path);

		vTmp.set(cell.x,cell.y).sub(cell2.x, cell2.y).rotate90(-1).nor().scl(10).add(cell.x, cell.y);

		drawBubble(lineColor, travelTimeSeconds + "", cell.x, cell.y, (int) vTmp.x, (int) vTmp.y, layerManager.getTeamNavLayer(team).pixmap, new RenderMask(team == Team.MARINE ? RenderMask.Mask.PATHFIND_MARINE : RenderMask.Mask.PATHFIND_ALIEN));
	}

	/**
	 /** Render lined bubble in pixmap space.
	 *
	 * @param color
	 * @param text
	 * @param x1 line origin
	 * @param y1 line origin
	 * @param x2 line end (where bubble will be)
	 * @param y2 line end (where bubble will be)
	 * @param pixmap pixmap to render to.
	 * @param renderMask render mask for label.
	 */
	public void drawBubble(Color color, String text, int x1, int y1, int x2, int y2, Pixmap pixmap, RenderMask renderMask) {

		pixmap.setColor(color);
		pixmap.drawLine(
				x1, pixmap.getHeight() - y1,
				x2, pixmap.getHeight() - y2);

		tmpCol.set(color).a = 1f;
		pixmap.setColor(tmpCol);
		pixmap.fillRectangle(x2 - 6, pixmap.getHeight() - y2 - 4, 11, 8);

		// label is in screen space.
		Label label = new Label(text);
		label.scale = 2;
		label.align = Label.Align.CENTER;
		new EntityBuilder(world).with(
				new Renderable(1000),
				new Transient(),
				new net.mostlyoriginal.api.component.graphics.Color(1f,1f,1f,1f),
				renderMask,
				new Pos((int)(x2* LayerManager.CELL_SIZE), (int)(y2 * LayerManager.CELL_SIZE)),
				label)
				.build();
	}
}
