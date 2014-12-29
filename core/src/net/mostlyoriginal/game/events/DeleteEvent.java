package net.mostlyoriginal.game.events;

import com.artemis.Entity;
import net.mostlyoriginal.api.event.common.Event;

/**
 * Entity was deleted (via Deletable).
 *
 * @author Daan van Yperen
 */
public class DeleteEvent implements Event {
	public Entity entity;

	public DeleteEvent(Entity entity) {
		this.entity = entity;
	}
}
