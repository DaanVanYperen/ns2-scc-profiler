package net.mostlyoriginal.game.system.logic;

import net.mostlyoriginal.game.component.Team;

/**
* @author Daan van Yperen
*/
public class Element {
	public String id;
	public int x;
	public int y;
	public Team team;

	public Element() {
	}

	public Element(String id, int x, int y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}
}
