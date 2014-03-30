package client;

import common.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * Individual player run as a separate thread to allow
 * updates immediately the bat is moved
 */
class Player extends Thread {

    private C_PongModel pongModel;
    private NetObjectReader nor;
    private List<Long> pings;
    private long lastTimestamp;
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

            GameObject playerOneBat = (GameObject) state[0];
            GameObject playerTwoBat = (GameObject) state[1];
            GameObject ball         = (GameObject) state[2];

            if (playerId != -1) {

                // Timestamp is sent in the form leftTimestamp:rightTimestamp
                long timestamp          =  Long.parseLong(state[3].toString().
                        split(":")[playerId], 10);

                long ping               = System.currentTimeMillis() - timestamp;

                if (lastTimestamp != timestamp) {

                    addPing(ping);

                    pongModel.setAveragePing(averagePing());
                    pongModel.setLastRequestRTT(ping);

                }

                lastTimestamp = timestamp;

            }

            pongModel.setBats(new GameObject[] {playerOneBat, playerTwoBat});
            pongModel.setBall(ball);

            pongModel.modelChanged();

        }

    }

    private void addPing(long ping) {

        if (pings.size() >= 50) {

            pings.remove(0);
            pings.add(ping);

        } else {

            pings.add(ping);

        }

    }

    private long averagePing() {

        int a = 0;

        for (Long ping : pings) a += ping;

        return a / pings.size();

    }

}
