package net.mostlyoriginal.game.api;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.ImmutableBag;
import net.mostlyoriginal.game.manager.LayerManager;

import java.util.LinkedList;

/**
 * Break up large tasks into atomic runnables, and chain other prerequisite systems.
 *
 * @author Daan van Yperen
 */
public abstract class DelayedEntitySystem extends EntitySystem {

	private boolean dirty = true;
	private boolean idle = true;

	protected LayerManager layerManager;
	private ImmutableBag<Entity> entities;
	private DelayedEntitySystem[] prerequisites = new DelayedEntitySystem[0];

	public DelayedEntitySystem(Aspect aspect)
	{
		super(aspect);
	}

	private LinkedList<Runnable> jobs = new LinkedList<>();

	@Override
	protected void processEntities(ImmutableBag<Entity> entities ) {

		if (dirty) {
			if ( !prerequisitesMet()) return;

			dirty = false;
			jobs.clear();
			collectJobs(entities, jobs);
		}

		// run a single job, if any.
		while ( !jobs.isEmpty()) {
			Runnable runnable = jobs.pollFirst();
			if (runnable != null) {
				runnable.run();
			}
		}

		idle = jobs.isEmpty();
	}

	private boolean prerequisitesMet() {
		for (DelayedEntitySystem prerequisite : prerequisites) {
			// parent systems need to be done computing completely.
			if ( prerequisite.isDirty() || !prerequisite.isIdle() )
				return false;
		}
		return true;
	}

	/**
	 * Gather jobs, perform initial jobs.
	 *
	 * Called when this system is dirty and after all prerequisite systems are done processing.
	 *
	 * @param entities
	 * @param jobs
	 */
	protected abstract void collectJobs(ImmutableBag<Entity> entities, LinkedList<Runnable> jobs);

	/** Requires a rerun. */
	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/** Done with all jobs. */
	public boolean isIdle() {
		return idle;
	}

	/** Job systems that need to be done processing before this system can process. */
	public void setPrerequisiteSystems(DelayedEntitySystem... prerequisites) {
		this.prerequisites = prerequisites;
	}
}
