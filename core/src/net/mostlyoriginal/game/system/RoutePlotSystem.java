package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.Path;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.manager.LayerManager;

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

	private void renderPaths(Routable routable, Team team) {
		List<Path> paths = routable.paths.get(team);
		for (Path path : paths) {
			// don't render the reverse paths.
			if (path.preferred && !path.reversed) {
				layerManager.getTeamNavLayer(team).drawPath(path, team.getPathColor());
			}
		}
	}

}
