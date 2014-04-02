package client;

import common.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Individual player run as a separate thread to allow
 * updates the moment the bat is moved
 */
class Player extends Thread {

    /**
     * The pong model
     */
    protected C_PongModel pongModel;

    /**
     * Used to receive updates from the server
     */
    protected NetObjectReader nor;

    /**
     * A list of round trip times (pings)
     */
    private List<Long> pings;

    /**
     * The last timestamp sent to the server
     */
    private long lastTimestampReceived;

    /**
     * The players id
     */
    private int playerId;

    /**
     * Constructor
     *
     * @param model - model of the game
     * @param nor     - Socket used to communicate with server
     */
    public Player(C_PongModel model, NetObjectReader nor, int playerId) {

        // The player needs to know this to be able to work
        pongModel = model;
        this.nor = nor;
        this.playerId = playerId;
        pings = new ArrayList<Long>();

    }

    /**
     * Get and update the model with the latest bat movement
     * sent by the server
     */
    public void run() {

        // Listen to network to get the latest state of the game from the server
        // Update model with this information, Redisplay model
        DEBUG.trace("Player.run");

        while (true) {

            Object o = nor.get();

            Serializable[] state = (Serializable[]) o;

            // Timestamp is sent in the form leftTimestamp:rightTimestamp
            long timestamp = Long.parseLong(state[3].toString().
                    split(Global.DELIMITER)[playerId], 10);

            long ping = System.currentTimeMillis() - timestamp;

            // Stop the ping rapidly increasing by keeping track of the last
            // timestamp received by the server therefore only calculating
            // a ping the first time a timestamp occurs
            if (lastTimestampReceived != timestamp) {

                addPing(ping);

                pongModel.setAveragePing(averagePing());
                pongModel.setLastRequestRTT(ping);

            }

            lastTimestampReceived = timestamp;

            refreshModel(state);

        }

    }

    /**
     * Refresh the common elements of the model (that both spectators and
     * players share)
     *
     * @param state the update sent from the server
     */
    protected synchronized void refreshModel(Serializable[] state) {

        GameObject playerOneBat = (GameObject) state[0];
        GameObject playerTwoBat = (GameObject) state[1];
        GameObject ball         = (GameObject) state[2];

        pongModel.setBats(new GameObject[] {playerOneBat, playerTwoBat});
        pongModel.setBall(ball);

        pongModel.modelChanged();

    }

    /**
     * Add a ping, keeps a store of the last 50 pings so we can average them.
     *
     * When a new ping is added, the oldest is removed.
     *
     * @param ping the ping to be average
     */
    private void addPing(long ping) {

        if (pings.size() >= Global.PING_LIMIT) pings.remove(0);

        pings.add(ping);

    }

    /**
     * Calculates the average ping of the most recent pings
     *
     * @return the average ping of the client
     */
    private long averagePing() {

        int a = 0;

        for (Long ping : pings) a += ping;

        return a / pings.size();

    }

}
