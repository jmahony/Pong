package common;

/**
 * Major constants used in the game
 */
public class Global {

    /**
     * The height of the window
     */
    public static final int H = 450;

    /**
     * The width of the window
     */
    public static final int W = 600;

    /**
     * Border offset
     */
    public static final double B = 6;

    /**
     * Menu offset
     */
    public static final double M = 26;

    /**
     * The size of the ball
     */
    public static final double BALL_SIZE = 15;

    /**
     * The width of the bat
     */
    public static final double BAT_WIDTH = 10;

    /**
     * The height of the bat
     */
    public static final double BAT_HEIGHT = 100;

    /**
     * The amount the bat should move with each keypress
     */
    public static final double BAT_MOVE = 5;

    /**
     * Which port the should be used, this should be passed in as a parameter to
     * both client and server
     */
    public static int port = 50001;       // Port

    /**
     * The servers IP
     */
    public static String host = "localhost";

    /**
     * The IP
     */
    public static final String MC_ADDRESS = "224.0.0.7";

    /**
     * The max amount of players on the server
     */
    public static final int MAX_PLAYERS = 4;

    /**
     * Helper constants for the player ids, so we are always calling the players
     * either left or right rather than 0 or 1. -1 is a spectator
     */
    public static final int SPECTATOR    = -1;
    public static final int LEFT_PLAYER  = 0;
    public static final int RIGHT_PLAYER = 1;

    /**
     * Whether or not to enable delay compensation on the server
     */
    public static boolean delay_compensation = true;

    /**
     * Delimiter to split string on
     */
    public static final String DELIMITER = ":";

    /**
     * How many pings to keep for the average
     */
    public static final int PING_LIMIT = 50;

}

