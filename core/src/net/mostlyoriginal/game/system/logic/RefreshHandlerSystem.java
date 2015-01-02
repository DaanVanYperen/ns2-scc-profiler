package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import net.mostlyoriginal.api.event.common.Subscribe;
import net.mostlyoriginal.api.utils.EntityUtil;
import net.mostlyoriginal.game.component.ui.Transient;
import net.mostlyoriginal.game.events.DeleteEvent;
import net.mostlyoriginal.game.events.DragEvent;
import net.mostlyoriginal.game.events.TeamChangeEvent;
import net.mostlyoriginal.game.manager.EntityFactoryManager;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.system.logic.analysis.NavigationGridCalculationSystem;
import net.mostlyoriginal.game.system.logic.analysis.PreferredRouteCalculationSystem;
import net.mostlyoriginal.game.system.logic.analysis.RouteCalculationSystem;
import net.mostlyoriginal.game.system.render.layer.DomainSystem;
import net.mostlyoriginal.game.system.render.layer.RoutePlotSystem;
import net.mostlyoriginal.game.system.render.layer.TechpointPressureSystem;
import net.mostlyoriginal.game.system.render.layer.TechpointSymmetrySystem;

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
	private NavigationGridCalculationSystem navigationGridCalculationSystem;

	public RefreshHandlerSystem() {
		super(Aspect.getAspectForAll(Transient.class));
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {

	}

	@Subscribe
	public void listenerMoved(DragEvent event) {
		restart();
	}

	@Subscribe
	public void listenerMoved(DeleteEvent event) {
		restart();
	}

	@Subscribe
	public void listenerMoved(TeamChangeEvent event) {
		restart();
	}


	/**
	 * restart all generation.
	 */
	public void restart() {
		clearRoutes();
	}

	private void clearRoutes() {
		// Core Dependencies
		navigationGridCalculationSystem.setDirty(true);
		routeCalculationSystem.setDirty(true);
		preferredRouteCalculationSystem.setDirty(true);

		// Separate view specific:
		routePlotSystem.setDirty(true);
		techpointSymmetrySystem.setDirty(true);
		techpointPressureSystem.setDirty(true);
		domainSystem.setDirty(true);
	}

	public void purgeAllTransientEntities() {
		EntityUtil.safeDeleteAll(getActives());
	}
}
