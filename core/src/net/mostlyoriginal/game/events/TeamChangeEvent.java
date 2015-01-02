package net.mostlyoriginal.game.events;

import com.artemis.Entity;
import net.mostlyoriginal.api.event.common.Event;

/**
 * Team changed.
 *
 * @author Daan van Yperen
 */
public class TeamChangeEvent implements Event {
	public Entity entity;

	public TeamChangeEvent(Entity entity) {
		this.entity = entity;
	}
}
