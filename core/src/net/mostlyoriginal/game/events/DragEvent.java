package net.mostlyoriginal.game.events;

import com.artemis.Entity;
import net.mostlyoriginal.api.event.common.Event;

/**
 * Entity was dragged.
 *
 * @author Daan van Yperen
 */
public class DragEvent implements Event {
	public Entity entity;

	public DragEvent(Entity entity) {
		this.entity = entity;
	}
}
