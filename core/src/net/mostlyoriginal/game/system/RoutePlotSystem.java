package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.game.G;
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

			// render all paths on team layers.
			for(int i=0,s=entities.size();i<s;i++)
			{
				final Entity e = entities.get(i);
				final Routable routable = mRoutable.get(e);

				for (Team team : Team.values()) {
					renderPaths(routable, team);
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
				tmpCol.set(team.getPathColor());
				tmpCol.r = tmpCol.r * MathUtils.random(0.4f,1f) + MathUtils.random(0f,0.1f);
				tmpCol.g = tmpCol.g * MathUtils.random(0.4f,1f) + MathUtils.random(0f,0.1f);
				tmpCol.b = tmpCol.b * MathUtils.random(0.4f,1f) + MathUtils.random(0f,0.1f);

				tmpCol.clamp();

				layerManager.getTeamNavLayer(team).drawPath(path, tmpCol);

				addLabel(team, path);
			}
		}
	}


	private void addLabel(Team team, Path path) {
		int center = path.cells.size() / 2;
		GridCell cell = path.cells.get(center);

		int travelTimeSeconds = Math.round((path.getPixelLength() * G.PIXELS_TO_UNITS) / team.getAvgSpeed());


		Label label = new Label(travelTimeSeconds + "");
		label.scale = 2;
		new EntityBuilder(world).with(
				new Renderable(1000),
				new Transient(),
				new RenderMask(team == Team.MARINE ? RenderMask.Mask.PATHFIND_MARINE : RenderMask.Mask.PATHFIND_ALIEN),
				new Pos(cell.getX() * LayerManager.CELL_SIZE, cell.getY() * LayerManager.CELL_SIZE),
				label)
				.build();
	}
}
