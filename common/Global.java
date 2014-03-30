package common;

/**
 * Major constants used in the game
 */
public class Global {
    public static final int H = 450;          // Height of window
    public static final int W = 600;          // Width  of window

    public static final double B = 6;            // Border offset
    public static final double M = 26;           // Menu offset
    public static final double BALL_SIZE = 15;  // Ball side
    public static final double BAT_WIDTH = 10;  // Bat width
    public static final double BAT_HEIGHT = 100; // Bat Height

    public static final double BAT_MOVE = 5;       // Each move is


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
     * The max amount of players on the server
     */
    public static final int MAX_PLAYERS = 4;

    /**
     * Helper constants for the player ids, so we are always calling the players
     * either left or right rather than 0 or 1.
     */
    public static final int LEFT_PLAYER = 0;
    public static final int RIGHT_PLAYER = 1;

    /**
     * Whether or not to enable delay compensation on the server
     */
    public static final boolean DELAY_COMPENSATION = true;

}

