package net.mostlyoriginal.game.system.render.layer;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.game.Path;
import net.mostlyoriginal.game.api.DelayedEntitySystem;
import net.mostlyoriginal.game.api.pathfinding.grid.GridNode;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.component.ui.Clickable;
import net.mostlyoriginal.game.component.ui.Label;
import net.mostlyoriginal.game.component.ui.RenderMask;
import net.mostlyoriginal.game.component.ui.Transient;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.manager.MapMetadataManager;
import net.mostlyoriginal.game.system.logic.RenderMaskHandlerSystem;
import net.mostlyoriginal.game.system.logic.analysis.PreferredRouteCalculationSystem;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Daan van Yperen
 */
@Wire(injectInherited = true)
public class RoutePlotSystem extends DelayedEntitySystem {

	protected LayerManager layerManager;

	protected ComponentMapper<Routable> mRoutable;

	private PreferredRouteCalculationSystem preferredRouteCalculationSystem;
	private RenderMaskHandlerSystem renderMaskHandlerSystem;
	private MapMetadataManager mapMetadataManager;

	@SuppressWarnings("unchecked")
	public RoutePlotSystem() {
		super(Aspect.getAspectForAll(Routable.class, Pos.class));
	}

	@Override
	protected void initialize() {
		super.initialize();
		setPrerequisiteSystems(preferredRouteCalculationSystem);
	}

	@Override
	protected boolean prerequisitesMet() {
		// only render when on the right layer.
		return super.prerequisitesMet() && (renderMaskHandlerSystem.getActiveMask() == RenderMask.Mask.PATHFIND_ALIEN || renderMaskHandlerSystem.getActiveMask() == RenderMask.Mask.PATHFIND_MARINE);
	}

	@Override
	protected void collectJobs(ImmutableBag<Entity> entities, LinkedList<Job> jobs) {

		// render all secondary paths as shadows.
		for (int i = 0, s = entities.size(); i < s; i++) {
			final Entity e = entities.get(i);
			final Routable routable = mRoutable.get(e);

			for (Team team : Team.values()) {
				renderSecondaryPaths(routable, team);
			}
		}

		// render all primary paths on team layers.
		for (int i = 0, s = entities.size(); i < s; i++) {
			final Entity e = entities.get(i);
			final Routable routable = mRoutable.get(e);

			for (Team team : Team.values()) {
				renderPaths(routable, team);
			}
		}
	}

	@Override
	protected void postJobs() {
		for (Team team : Team.values()) {
			layerManager.getTeamNavLayer(team).invalidateTexture();
		}
	}

	Color tmpCol = new Color();

	private void renderPaths(Routable routable, Team team) {
		List<Path> paths = routable.paths.get(team);
		for (Path path : paths) {
			// don't render the reverse paths.
			if (path.preferred && !path.reversed) {
				renderPath(path, layerManager.getTeamNavLayer(team), team.getPathColor(), new RenderMask(path.team == Team.MARINE ? RenderMask.Mask.PATHFIND_MARINE : RenderMask.Mask.PATHFIND_ALIEN), true);
			}
		}
	}

	//
	public void renderPath(Path path, Layer layer, Color color, RenderMask renderMask, boolean renderLabel) {
		// slightly vary path color to make it easier to track.

		path.color.set(color);
		path.color.r = path.color.r * MathUtils.random(0.4f, 1f) + MathUtils.random(0f, 0.1f);
		path.color.g = path.color.g * MathUtils.random(0.4f, 1f) + MathUtils.random(0f, 0.1f);
		path.color.b = path.color.b * MathUtils.random(0.4f, 1f) + MathUtils.random(0f, 0.1f);
		path.color.a = 0.8f;
		path.color.clamp();

		layer.drawPath(path, path.color, true);

		if (renderLabel) {
			int travelTime = path.team.getTravelTimeInSeconds(path, mapMetadataManager.getMetadata().unitsPerPixel);
			addLabel(path.color, path, layer.pixmap,
					renderMask, travelTime + "",
							new DistanceIndicator(path.getPixelLength(), path.team.getAvgSpeed(), travelTime ));
		}
	}

	private void renderSecondaryPaths(Routable routable, Team team) {
		List<Path> paths = routable.paths.get(team);
		for (Path path : paths) {
			// don't render the reverse paths.
			if (!path.preferred && !path.reversed) {

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

	private Vector2 vTmp = new Vector2();


	/**
	 * Add label somewhere halfway along the given path.
	 */
	private void addLabel(Color lineColor, Path path, Pixmap pixmap, RenderMask renderMask, String text, DistanceIndicator distanceIndicator) {

		int center = path.cells.size() / 2;
		GridNode cell = path.cells.get(center)  ;

		// use a couple distance to get a smoother angle.
		GridNode cell2 = center + 3 < path.cells.size() ? path.cells.get(center + 3) : cell;

		vTmp.set(cell.x, cell.y).sub(cell2.x, cell2.y).rotate90(-1).nor().scl(10).add(cell.x, cell.y);
		drawBubble(lineColor, text, cell.x, cell.y, (int) vTmp.x, (int) vTmp.y, pixmap, renderMask, distanceIndicator);
	}

	/**
	 * /** Render lined bubble in pixmap space.
	 *  @param color
	 * @param text
	 * @param x1         line origin
	 * @param y1         line origin
	 * @param x2         line end (where bubble will be)
	 * @param y2         line end (where bubble will be)
	 * @param pixmap     pixmap to render to.
	 * @param renderMask render mask for label.
	 * @param distanceIndicator
	 */
	public void drawBubble(Color color, String text, int x1, int y1, int x2, int y2, Pixmap pixmap, RenderMask renderMask, DistanceIndicator distanceIndicator) {

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
		Entity e = new EntityBuilder(world).with(
				new Renderable(1000),
				new Transient(),
				new net.mostlyoriginal.api.component.graphics.Color(1f, 1f, 1f, 1f),
				renderMask,
				new Input(1, 2),
				new Bounds(0, 0, 11, 8),
				new Clickable(),
				new Pos((int) (x2 * LayerManager.CELL_SIZE), (int) (y2 * LayerManager.CELL_SIZE)),
				label)
				.build();
		if ( distanceIndicator != null ) {
			e.edit().add(distanceIndicator);
		}
	}
}
