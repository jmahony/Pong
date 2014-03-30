package client;

import java.util.Observable;

import common.GameObject;

import static common.Global.*;

/**
 * Model of the game of pong (Client)
 */
public class C_PongModel extends Observable {

    /**
     * The games ball
     */
    private GameObject ball = new GameObject(W / 2, H / 2, BALL_SIZE, BALL_SIZE);

    /**
     * The games bats
     */
    private GameObject bats[] = new GameObject[2];

    /**
     * The average ping
     */
    private long averagePing;

    /**
     * The last requests round trip time
     */
    private long lastRequestRTT;

    /**
     * Constructor
     */
    public C_PongModel() {
        bats[0] = new GameObject(60, H / 2, BAT_WIDTH, BAT_HEIGHT);
        bats[1] = new GameObject(W - 60, H / 2, BAT_WIDTH, BAT_HEIGHT);
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
     * Return the Game object representing the Bats for player
     *
     * @return Array of two bats
     */
    public GameObject[] getBats() {
        return bats;
    }

    /**
     * Set the Bats used
     *
     * @param theBats - Players Bat
     */
    public void setBats(GameObject[] theBats) {
        bats = theBats;
    }

    /**
     * Sets the average ping
     *
     * @param averagePing the average ping
     */
    public void setAveragePing(long averagePing) {
        this.averagePing = averagePing;
    }

    /**
     * Returns the average ping
     *
     * @return the average ping
     */
    public long getAveragePing() {
        return averagePing;
    }

    /**
     * Gets the last requests round trip time
     *
     * @return the round trip time of the last request
     */
    public long getLastRequestRTT() { return lastRequestRTT; }

    /**
     * Sets the last requests round time time
     *
     * @param lastRequestRTT the last requests round trip time
     */
    public void setLastRequestRTT(long lastRequestRTT) { this.lastRequestRTT = lastRequestRTT; }

    /**
     * Cause update of view of game
     */
    public void modelChanged() {
        setChanged();
        notifyObservers();
    }

}
