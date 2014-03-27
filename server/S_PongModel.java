package server;

import common.*;

import static common.Global.*;

import java.util.Observable;

/**
 * Model of the game of pong
 * The active object ActiveModel does the work of moving the ball
 */
public class S_PongModel extends Observable {
    private GameObject ball = new GameObject(W / 2, H / 2, BALL_SIZE, BALL_SIZE);
    private GameObject bats[] = new GameObject[2];

    private Thread activeModel;

    private long[] pings     = new long[2];
    private long[] avgPings  = new long[2];

    /**
     * Which player needs the view update delayed
     */
    public int playerToDelay = -1;

    /**
     * How many ms the last delay was, so we can calculate the players
     * ring round trip time
     */
    public int artificalDelayAmount = 0;

    /**
     * How many ticks the player should miss
     */
    public int delayAmount = -1;

    public S_PongModel() {
        bats[0] = new GameObject(60, H / 2, BAT_WIDTH, BAT_HEIGHT);
        bats[1] = new GameObject(W - 60, H / 2, BAT_WIDTH, BAT_HEIGHT);
        activeModel = new Thread(new S_ActiveModel(this));
    }

    /**
     * Start the thread that moves the ball and detects collisions
     */
    public void makeActiveObject() {
        activeModel.start();
    }

    /**
     * Return the Game object representing the ball
     *
     * @return the ball
     */
    public GameObject getBall() {
        return ball;
    }

    /**
     * Set a new Ball object
     *
     * @param aBall - Ball to be set
     */
    public void setBall(GameObject aBall) {
        ball = aBall;
    }

    /**
     * Return the Game object representing the Bat for player
     *
     * @param player 0 or 1
     */
    public GameObject getBat(int player) {
        return bats[player];
    }

    /**
     * Return the Game object representing the Bats
     *
     * @return Array of two bats
     */
    public GameObject[] getBats() {
        return bats;
    }

    /**
     * Set the Bat for a player
     *
     * @param player 0 or 1
     * @param theBat Players Bat
     */
    public void setBat(int player, GameObject theBat) {
        bats[player] = theBat;
    }

    /**
     *
     * @param player
     * @param _ping
     */
    public void setPing(int player, long _ping) {
        pings[player] = _ping;
    }

    /**
     *
     * @param player
     * @return
     */
    public long getPing(int player) {
        return pings[player];
    }

    /**
     *
     * @param player
     * @param _ping
     */
    public void setAvgPing(int player, long _ping) {
        avgPings[player] = _ping;
    }

    /**
     *
     * @param player
     * @return
     */
    public long getAvgPing(int player) {
        return avgPings[player];
    }

    /**
     *
     * Decides which player to delay and by how many game ticks
     *
     * @return index position of player to delay
     */
    public void setPlayerToDelay() {

        long p0 = getAvgPing(0),
             p1 = getAvgPing(1);

        if (p0 == p1) {

            playerToDelay = -1; // Delay neither
            delayAmount = -1;

        } else if(p0 > p1) {

            // Delay player 0
            playerToDelay = 1;
            p1 = p1 - artificalDelayAmount;
            artificalDelayAmount = (int)(p0 - p1);
            delayAmount = (artificalDelayAmount / 2) / GAME_TICK;


        } else {

            // Delay player 1
            playerToDelay = 0;
            p0 = p0 - artificalDelayAmount;
            artificalDelayAmount = (int)(p1 - p0) / 2;
            delayAmount = artificalDelayAmount / GAME_TICK;

        }

    }

    /**
     * Cause update of view of game
     */
    public void modelChanged() {
        DEBUG.trace("S_PongModel.modelChanged");
        setChanged();
        notifyObservers();
    }

}
