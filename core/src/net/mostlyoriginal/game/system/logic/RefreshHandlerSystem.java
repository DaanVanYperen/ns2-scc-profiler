package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import net.mostlyoriginal.api.utils.EntityUtil;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.component.ui.ButtonListener;
import net.mostlyoriginal.game.component.ui.Transient;
import net.mostlyoriginal.game.manager.EntityFactoryManager;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.system.PreferredRouteCalculationSystem;
import net.mostlyoriginal.game.system.RouteCalculationSystem;
import net.mostlyoriginal.game.system.render.ClosestTechpointPlotSystem;
import net.mostlyoriginal.game.system.render.RoutePlotSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class RefreshHandlerSystem extends EntitySystem {

	EntityFactoryManager entityFactoryManager;

	private LayerManager layerManager;
	private RouteCalculationSystem routeCalculationSystem;
	private RoutePlotSystem routePlotSystem;
	private PreferredRouteCalculationSystem preferredRouteCalculationSystem;
	private ClosestTechpointPlotSystem closestTechpointPlotSystem;

	public RefreshHandlerSystem() {
		super(Aspect.getAspectForAll(Transient.class));
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {

	}

	@Override
	protected void initialize() {
		super.initialize();

		entityFactoryManager.createBasicButton("refresh", 50 + 40 * 5, new ButtonListener() {
			@Override
			public void run() {
				restart();
			}

			@Override
			public boolean enabled() {
				return true;
			}
		});
	}

	/** restart all generation. */
	public void restart() {

		purgeAllTransientEntities();
		clearRoutes();
		clearTeamLayers();
	}

	private void clearRoutes() {
		routeCalculationSystem.dirty=true;
		preferredRouteCalculationSystem.dirty=true;
		routePlotSystem.dirty=true;
		closestTechpointPlotSystem.dirty=true;
	}

	private void clearTeamLayers() {
		for (Team team : Team.values()) {
			layerManager.getTeamNavLayer(team).clear();
		}
	}

	private void purgeAllTransientEntities() {
		EntityUtil.safeDeleteAll(getActives());
	}
}
