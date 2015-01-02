package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.artemis.utils.ImmutableBag;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Color;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.event.common.Subscribe;
import net.mostlyoriginal.api.utils.EntityUtil;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.api.DelayedEntitySystem;
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

import java.util.LinkedList;

/**
 * @author Daan van Yperen
 */
@Wire
public class RefreshHandlerSystem extends DelayedEntitySystem {

	EntityFactoryManager entityFactoryManager;

	private LayerManager layerManager;
	private RouteCalculationSystem routeCalculationSystem;
	private RoutePlotSystem routePlotSystem;
	private PreferredRouteCalculationSystem preferredRouteCalculationSystem;
	private TechpointSymmetrySystem techpointSymmetrySystem;
	private TechpointPressureSystem techpointPressureSystem;
	private DomainSystem domainSystem;
	private NavigationGridCalculationSystem navigationGridCalculationSystem;
	private Entity refreshIndicator;

	protected ComponentMapper<Angle> mAngle;

	public RefreshHandlerSystem() {
		super(Aspect.getAspectForAll(Transient.class));
	}

	@Override
	protected void initialize() {
		Anim refresh = new Anim("refresh");
		refresh.scale = 5;
		refreshIndicator = new EntityBuilder(world).with(new Pos(0, G.CANVAS_HEIGHT-120), new Color(1f,1f,1f,0.5f), new Angle(0,40,40), refresh, new Renderable(4000)).build();
		setPrerequisiteSystems(preferredRouteCalculationSystem);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		Angle angle = mAngle.get(refreshIndicator);
		angle.rotation += world.delta * -400f;
		super.processEntities(entities);
	}

	@Override
	protected void collectJobs(ImmutableBag<Entity> entities, LinkedList<Job> jobs) {
		refreshIndicator.edit().add(new Invisible());
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

		setDirty(true);
		refreshIndicator.edit().remove(Invisible.class);

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
