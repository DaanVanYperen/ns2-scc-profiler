package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import net.mostlyoriginal.api.event.common.Subscribe;
import net.mostlyoriginal.api.utils.EntityUtil;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.component.ui.ButtonListener;
import net.mostlyoriginal.game.component.ui.Transient;
import net.mostlyoriginal.game.events.DragEvent;
import net.mostlyoriginal.game.manager.EntityFactoryManager;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.system.PreferredRouteCalculationSystem;
import net.mostlyoriginal.game.system.render.DomainSystem;
import net.mostlyoriginal.game.system.render.TechpointPressureSystem;
import net.mostlyoriginal.game.system.render.TechpointSymmetrySystem;
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
	private TechpointSymmetrySystem techpointSymmetrySystem;
	private TechpointPressureSystem techpointPressureSystem;
	private DomainSystem domainSystem;

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
		}, 50);
	}

	@Subscribe
	public void listenerMoved( DragEvent event )
	{
		restart();
	}


	/** restart all generation. */
	public void restart() {

		purgeAllTransientEntities();
		clearRoutes();
		clearTeamLayers();
	}

	private void clearRoutes() {
		// Core Dependencies
		routeCalculationSystem.setDirty(true);
		preferredRouteCalculationSystem.dirty=true;

		// Separate view specific:
		routePlotSystem.dirty=true;
		techpointSymmetrySystem.dirty=true;
		techpointPressureSystem.dirty=true;
		domainSystem.dirty=true;
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
