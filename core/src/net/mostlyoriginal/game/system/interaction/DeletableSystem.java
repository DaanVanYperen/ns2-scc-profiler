package net.mostlyoriginal.game.system.interaction;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.event.common.EventManager;
import net.mostlyoriginal.game.component.Deletable;
import net.mostlyoriginal.game.component.ui.Clickable;
import net.mostlyoriginal.game.events.DeleteEvent;

/**
 * @author Daan van Yperen
 */
@Wire
public class DeletableSystem extends EntityProcessingSystem {

	protected ComponentMapper<Clickable> mClickable;
	protected EventManager em;

	public DeletableSystem() {
		super(Aspect.getAspectForAll(Pos.class, Clickable.class, Deletable.class));
	}

	@Override
	protected void process(Entity e) {
		if ( mClickable.get(e).state == Clickable.ClickState.CLICKED_RIGHT ) {
			em.dispatch(new DeleteEvent(e));
			e.deleteFromWorld();
		}
	}
}
