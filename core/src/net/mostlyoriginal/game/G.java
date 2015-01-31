package net.mostlyoriginal.game;

/**
 * @author Daan van Yperen
 */
public class G {

	public static String TITLE = "NS2 SCC Profiler";

	public static final int CANVAS_WIDTH = 800;
    public static final int CANVAS_HEIGHT = 800;

	// speed (in units per second)
    public static final float MARINE_SPEED_AVG = 6.25f; // marine can keep this speed consistently.
	public static final float ALIEN_SPEED_AVG = 9f; // speed is harder for aliens.

    public static final float UNITS_PER_PIXEL = 0.62f; // based on caged. Will vary per map.
}
