package net.mostlyoriginal.game.system.render.layer;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.Path;
import net.mostlyoriginal.game.api.DelayedEntitySystem;
import net.mostlyoriginal.game.component.Layer;
import net.mostlyoriginal.game.component.Persistable;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.component.buildings.ResourceNode;
import net.mostlyoriginal.game.component.buildings.Techpoint;
import net.mostlyoriginal.game.component.ui.RenderMask;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.system.logic.analysis.PreferredRouteCalculationSystem;

import java.util.LinkedList;

/**
 * Techpoint Symmetry
 * <p/>
 * Per team, highlight the shortest paths from techpoint to resource towers, and paths up to 5 seconds longer.
 * Marines Highlighted in blue, aliens in red.
 * <p/>
 * Helps determine what techpoints have the most access to RTs for each team, in an unchallenged situation.
 * Since travel speed is asymmetrical the dynamics change depending on the claimed techpoints.
 *
 * @author Daan van Yperen
 */
@Wire(injectInherited = true)
public class TechpointSymmetrySystem extends DelayedEntitySystem {

	public static final float MAX_SYMMETRY_DISCREPANCY = 5f;
	protected LayerManager layerManager;

	protected ComponentMapper<Routable> mRoutable;
	protected ComponentMapper<Persistable> mPersistable;
	protected ComponentMapper<Techpoint> mTechpoint;
	protected ComponentMapper<RenderMask> mRenderMask;

	protected RoutePlotSystem routePlotSystem;
	private PreferredRouteCalculationSystem preferredRouteCalculationSystem;

	public TechpointSymmetrySystem() {
		super(Aspect.getAspectForAll(Routable.class, Pos.class, ResourceNode.class));
	}

	@Override
	protected void initialize() {
		setPrerequisiteSystems(preferredRouteCalculationSystem);
	}

	@Override
	protected void collectJobs(ImmutableBag<Entity> entities, LinkedList<Runnable> jobs) {
		layerManager.clearWithMap(getAlienLayer(), Color.WHITE, 0.3f);
		layerManager.clearWithMap(getMarineLayer(), Color.WHITE, 0.3f);

		for (int i = 0, s = entities.size(); i < s; i++) {
			final Entity e = entities.get(i);
			final Routable routable = mRoutable.get(e);

			plotCloseTechpoints(e, routable);
		}
	}

	private void plotCloseTechpoints(Entity e, Routable routable) {

		Layer layerAliens = getAlienLayer();
		Layer layerMarines = getMarineLayer();

		// build a list of all paths, sorted by TRAVEL TIME. which can be different based on the team running the path.
		for (Team team : Team.values()) {
			float closest = -1;
			for (Path path : routable.paths.get(team)) {
				Entity destination = path.destination.get();
				if (mTechpoint.has(destination)) {

					int travelTimeInSeconds = path.team.getTravelTimeInSeconds(path);

					if (closest == -1) closest = travelTimeInSeconds;

					// abort when paths are longer than 10% of the closest techpoint.
					if (travelTimeInSeconds > closest + MAX_SYMMETRY_DISCREPANCY)
						break;

					routePlotSystem.renderPath(path,
							path.team == Team.ALIEN ? layerAliens : layerMarines,
							path.team.getPathColor(),
							new RenderMask(path.team == Team.ALIEN ? RenderMask.Mask.RT_SYMMETRY_ALIEN : RenderMask.Mask.RT_SYMMETRY_MARINE), true);
				}
			}
		}
	}

	private Layer getMarineLayer() {
		return layerManager.getLayer("TECHPOINTS_MARINES", RenderMask.Mask.RT_SYMMETRY_MARINE);
	}

	private Layer getAlienLayer() {
		return layerManager.getLayer("TECHPOINTS_ALIENS", RenderMask.Mask.RT_SYMMETRY_ALIEN);
	}
}
