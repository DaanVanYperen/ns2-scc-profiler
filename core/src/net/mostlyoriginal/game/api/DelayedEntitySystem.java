package net.mostlyoriginal.game.api;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.utils.TimeUtils;
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

	public DelayedEntitySystem(Aspect aspect) {
		super(aspect);
	}

	private LinkedList<Job> jobs = new LinkedList<>();

	public static abstract interface Job extends Runnable {
		boolean isCompleted();
	}

	/**
	 * Maximum duration in which to start new jobs.
	 * <p/>
	 * Start new jobs within first job start time + maxDuration milliseconds.
	 * Not ideal, since new jobs might run far beyond the max duration, but it suffices.
	 */
	protected long maxDuration() {
		return 0;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {

		if (dirty) {
			if (!prerequisitesMet()) return;
			dirty = false;
			idle = false;
			jobs.clear();
			collectJobs(entities, jobs);
		}

		// run one or multiple times, until allotted time runs out.
		long start = TimeUtils.millis();
		long now = start;

		while (now <= start + maxDuration()) {

			Job runnable = !jobs.isEmpty() ? jobs.get(0) : null;
			if (runnable != null) {
				runnable.run();
				if (runnable.isCompleted()) {
					jobs.removeFirst();
				}
			}

			if (jobs.isEmpty()) {
				if ( !idle ) {
					postJobs();
					idle = true;
				}
				break;
			}

			now = TimeUtils.millis();
		}
	}

	// called right after all jobs are done processing.
	protected void postJobs() {
	}

	protected boolean prerequisitesMet() {
		for (DelayedEntitySystem prerequisite : prerequisites) {
			// parent systems need to be done computing completely.
			if (prerequisite.isDirty() || !prerequisite.isIdle())
				return false;
		}
		return true;
	}

	/**
	 * Gather jobs, perform initial jobs.
	 * <p/>
	 * Called when this system is dirty and after all prerequisite systems are done processing.
	 *
	 * @param entities
	 * @param jobs
	 */
	protected abstract void collectJobs(ImmutableBag<Entity> entities, LinkedList<Job> jobs);

	/**
	 * Requires a rerun.
	 */
	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * Done with all jobs.
	 */
	public boolean isIdle() {
		return idle;
	}

	/**
	 * Job systems that need to be done processing before this system can process.
	 */
	public void setPrerequisiteSystems(DelayedEntitySystem... prerequisites) {
		this.prerequisites = prerequisites;
	}
}
