package client;

import common.*;

import java.io.Serializable;

/**
 * Similar to the player class but does not extract any information from the
 * server payload, it just refreshes the model with the state
 */
public class Spectator extends Player {

    /**
     * Constructor
     *
     * @param model    - model of the game
     * @param nor      - reader used to communicate with server
     */
    public Spectator(C_PongModel model, NetMCReader nor) {

        super(model, nor, Global.SPECTATOR);

    }

    /**
     * Get and update the model with the latest bat movement
     * sent by the server
     */
    public void run() {

        DEBUG.trace("Player.run");

        while (true) {

            Object o = nor.get();

            Serializable[] state = (Serializable[]) o;

            refreshModel(state);

        }

    }

}
