package net.mostlyoriginal.game;

/**
 * @author Daan van Yperen
 */
public class G {

    public static final int CANVAS_WIDTH = 800;
    public static final int CANVAS_HEIGHT = 800;

    // speed (in units per second).
    public static final float marineSpeedSlow = 6f;
    public static final float MARINE_SPEED_FAST = 6.25f;
    public static final float MARINE_SPEED_AVG = 6.25f; // marine can keep this speed consistently.
    public static final float alienSpeedSlow  = 7.5f;
    public static final float ALIEN_SPEED_FAST = 11f;
    public static final float ALIEN_SPEED_AVG = 9f; // speed is harder for aliens.

    public static final float UNITS_PER_PIXEL = 0.62f; // CAGED
    //public static final float UNITS_PER_PIXEL = (1/0.95f) * (1/1.75f); //

    public static int MARINE_TEAM_SIZE = 8;
    public static int ALIEN_TEAM_SIZE = 8;
    public static String TITLE = "NS2 Yardstick";
}
