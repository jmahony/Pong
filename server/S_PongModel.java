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
    public long[] lastUpdateDelay = new long[2];

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
     * @param timestamp
     */
    public void setLastPingTimestamp(int player, long timestamp) {
        lastPingTimestamp[player] = timestamp;
    }

    /**
     *
     * @param player
     * @return
     */
    public long getLastPingTimestamp(int player) {
        return lastPingTimestamp[player];
    }

    /**
     *
     * @param player
     * @param averagePing
     */
    public void setAveragePing(int player, long averagePing) {
        averagePings[player] = averagePing;
    }

    /**
     *
     * @param player
     * @return
     */
    public long getAveragePing(int player) {
        return averagePings[player];
    }

    /**
     *
     * @param playerId
     * @param roundTripTime
     */
    public void setLastRequestRTT(int playerId, long roundTripTime) {
        lastRequestRTT[playerId] = roundTripTime;
    }

    /**
     *
     * @param playerId
     * @return
     */
    public long getLastRequestRTT(int playerId) {
        return lastRequestRTT[playerId];
    }

    public long getLastUpdateDelay(int playerId) {
        return lastUpdateDelay[playerId];
    }

    public void setLastUpdateDelay(int playerId, long delay) {
        // Make sure we don't store any negative values
        lastUpdateDelay[playerId] = delay >= 0 ? delay : 0;
    }

    public long[] getDelays() {

        // Get both players average ping
        // e.g. leftAvgPing = 200
        //      rightAvgPing = 200
        long leftAvgPing  = getAveragePing(Global.LEFT_PLAYER),
             rightAvgPing = getAveragePing(Global.RIGHT_PLAYER);

        // Calculate how much we need to delay each player.
        // One players delay will always be negative
        // so their update will be sent immediately
        // e.g. leftDelay  = 200 - 200 = 0
        //      rightDelay = 200 - 200 = 0
        long leftDelay  = (rightAvgPing - leftAvgPing),
             rightDelay = (leftAvgPing - rightAvgPing);

        // Calculate the actual delay (including the last delay)
        // e.g. Assuming the left player was delayed by 50ms last tick
        //     delay = (0 + 50) * 2 = 50 = 100ms delay
        //     delay = (0 + 0) * 2  = 0 = 0ms delay
        //     Anything created than or equal to 0 will treated as 0.
        long leftActualDelay  = leftDelay * 2 +
                getLastUpdateDelay(Global.LEFT_PLAYER),

             rightActualDelay = rightDelay * 2 +
                getLastUpdateDelay(Global.RIGHT_PLAYER);

        // Store how much we've delayed each player so we
        // can offset it against their delay on the next tick
        setLastUpdateDelay(Global.LEFT_PLAYER, leftDelay);
        setLastUpdateDelay(Global.RIGHT_PLAYER, rightDelay);

        return new long[] {leftActualDelay, rightActualDelay};

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
