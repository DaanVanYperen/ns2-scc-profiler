package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.component.Deletable;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.ui.Clickable;

/**
 * @author Daan van Yperen
 */
@Wire
public class DeletableSystem extends EntityProcessingSystem {

	protected ComponentMapper<Clickable> mClickable;

	public DeletableSystem() {
		super(Aspect.getAspectForAll(Pos.class, Clickable.class, Deletable.class));
	}

	@Override
	protected void process(Entity e) {
		if ( mClickable.get(e).state == Clickable.ClickState.CLICKED_RIGHT ) {
			e.deleteFromWorld();
		}
	}
}
