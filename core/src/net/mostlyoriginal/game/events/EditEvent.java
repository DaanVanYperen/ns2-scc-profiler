package net.mostlyoriginal.game.events;

import com.artemis.Entity;
import net.mostlyoriginal.api.event.common.Event;

/**
 * @author Daan van Yperen
 */
public class EditEvent implements Event {
	public Entity entity;

	public EditEvent(Entity entity) {
		this.entity = entity;
	}
}
