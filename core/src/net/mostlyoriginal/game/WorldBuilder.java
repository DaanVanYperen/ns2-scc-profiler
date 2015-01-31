package net.mostlyoriginal.game;

import com.artemis.EntitySystem;
import com.artemis.Manager;
import com.artemis.World;
import com.artemis.utils.Bag;

/**
 * World builder.
 *
 * @author Daan van Yperen
 */
public class WorldBuilder {

	Bag<Manager> managers = new Bag<>();
	Bag<SystemRegistration> systems = new Bag<>();

	public WorldBuilder() {
	}

	/** Return world, built. */
	public World build() {
		World world = new World();
		for (Manager manager : managers) {
			world.setManager(manager);
		}
		for (SystemRegistration system : systems) {
			world.setSystem( system.system, system.passive);
		}
		return world;
	}

	/** Return world, built and initialized */
	public World initialize()
	{
		World world = build();
		world.initialize();
		return world;
	}

	/** Add one or more managers to the world. */
	public WorldBuilder with(Manager ... managers) {
		for (Manager manager : managers) {
			this.managers.add(manager);
		}
		return this;
	}

	/** Add one or more managers to the world. */
	public WorldBuilder with(EntitySystem ... systems) {
		addSystems(systems, false);
		return this;
	}

	/** Register passive systems. */
	public WorldBuilder withPassive(EntitySystem ... systems) {
		addSystems(systems, true);
		return this;
	}

	private void addSystems(EntitySystem[] systems, boolean passive) {
		for (EntitySystem system : systems) {
			this.systems.add(new SystemRegistration(system, passive));
		}
	}

	public static class SystemRegistration {
		public final EntitySystem system;
		public final boolean passive;

		public SystemRegistration(EntitySystem system, boolean passive) {
			this.system = system;
			this.passive = passive;
		}
	}
}
