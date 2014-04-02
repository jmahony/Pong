package server;

import common.*;

import static common.Global.*;

import java.util.Observable;

/**
 * Model of the game of pong
 * The active object ActiveModel does the work of moving the ball
 */
public class S_PongModel extends Observable {

    /**
     * The games ball
     */
    private GameObject ball = new GameObject(W / 2, H / 2, BALL_SIZE, BALL_SIZE);

    /**
     * The players bats
     */
    private GameObject bats[] = new GameObject[2];

    /**
     * The active model of the game, this is the part of the game that keeps the
     * game movingforward
     */
    private Thread activeModel;

    /**
     * The last ping timestamp received from the players,
     * left is at position 0 and right is at position 1
     */
    private long[] lastPingTimestamp = new long[2];

    /**
     * The average pings for both players
     */
    private long[] averagePings = new long[2];

    /**
     * The last request round trip time, this isn't actually used by anything
     * but I thought i'd leave it in in case it becomes useful in the next
     * version
     */
    private long[] lastRequestRTT = new long[2];

    /**
     * The delay that was applied to the last update of the players game,
     * this is needed so we can estimate their real ping on the next tick.
     */
    private long[] lastUpdateDelay = new long[2];

    /**
     * Whether game is multicast or not
     */
    private boolean multicast = false;

    /**
     * Whether or not to enable delay compensation on the server
     */
    private boolean delayCompensation = true;

    /**
     * Constructor
     */
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
     * Sets the players last ping timestamp, this is used by the client to
     * calculate their round time time
     *
     * @param player the player
     * @param timestamp their timestamp
     */
    public void setLastPingTimestamp(int player, long timestamp) {
        lastPingTimestamp[player] = timestamp;
    }

    /**
     * Gets the players last ping timestamp
     *
     * @param playerId the player
     * @return their ping timestamp
     */
    public long getLastPingTimestamp(int playerId) {
        return lastPingTimestamp[playerId];
    }

    /**
     * Set the average ping of a player
     *
     * @param player the player
     * @param averagePing their ping
     */
    public void setAveragePing(int player, long averagePing) {
        averagePings[player] = averagePing;
    }

    /**
     * Get the average ping of a player
     *
     * @param playerId the player
     * @return their average ping
     */
    public long getAveragePing(int playerId) {
        return averagePings[playerId];
    }

    /**
     * Get the last requests round trip time
     *
     * @param playerId the player
     * @param roundTripTime their round trip time
     */
    public void setLastRequestRTT(int playerId, long roundTripTime) {
        lastRequestRTT[playerId] = roundTripTime;
    }

    /**
     * Get the last requests round trim time
     *
     * @param playerId the player
     * @return their round trip time
     */
    public long getLastRequestRTT(int playerId) {
        return lastRequestRTT[playerId];
    }

    /**
     * Get how much the last update was delayed by
     *
     * @param playerId the player
     * @return delay delay
     */
    public long getLastUpdateDelay(int playerId) {
        return lastUpdateDelay[playerId];
    }

    /**
     * Set how much the last request was delay by
     *
     * @param playerId the player
     * @param delay delay
     */
    public void setLastUpdateDelay(int playerId, long delay) {
        // Make sure we don't store any negative values
        lastUpdateDelay[playerId] = delay >= 0 ? delay : 0;
    }

    /**
     * Set whether the same is multicast or not
     *
     * @param b  whether to enable or not
     */
    public void setIsMultiCast(boolean b) {
        multicast = b;
    }

    /**
     * Whether the game is multicast
     *
     * @return whether game is multicast
     */
    public boolean isMulticast() {
        return multicast;
    }

    /**
     * Set delay compensation
     *
     * @param b whether to enable or not
     */
    public void setDelayCompensation(boolean b) {
        delayCompensation = b;
    }

    /**
     * Whether delay compensation is turn on or not
     *
     * @return whether delay compensation is turned on
     */
    public boolean isDelayCompensation() {
        return delayCompensation;
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
