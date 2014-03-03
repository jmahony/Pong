package client;

import common.*;
import static common.Global.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

/**
 * Individual player run as a separate thread to allow
 * updates immediately the bat is moved
 */
class Player extends Thread {

    private C_PongModel pongModel;
    private Socket socket;

    /**
     * Constructor
     *
     * @param model - model of the game
     * @param s     - Socket used to communicate with server
     */
    public Player(C_PongModel model, Socket s) {
        // The player needs to know this to be able to work
        pongModel = model;
        socket = s;
    }

    /**
     * Get and update the model with the latest bat movement
     * sent by the server
     */
    public void run() {

        // Listen to network to get the latest state of the game from the server
        // Update model with this information, Redisplay model
        DEBUG.trace("Player.run");

        try {

            NetObjectReader nor = new NetObjectReader(socket);

            while (true) {

                Object o = nor.get();

                Serializable[] state = (Serializable[]) o;
                
                GameObject playerOneBat = (GameObject) state[0];
                GameObject playerTwoBat = (GameObject) state[1];
                GameObject ball         = (GameObject) state[2];
                long timestamp          = (Long) state[3];
                
                pongModel.setBats(new GameObject[] {playerOneBat, playerTwoBat});
                pongModel.setBall(ball);
                pongModel.setTimestamp(timestamp);
                pongModel.modelChanged();

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

    }
}
