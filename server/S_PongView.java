package server;

import common.*;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;


/**
 * Displays a graphical view of the game of pong
 */
class S_PongView implements Observer {

    /**
     * Reference to the controller
     */
    private S_PongController pongController;

    /**
     * Reference to the ball
     */
    private GameObject ball;

    /**
     * Reference to the bats
     */
    private GameObject[] bats;

    /**
     * References to player 0 and 1 writers
     */
    private NetObjectWriter left, right;

    public S_PongView(NetObjectWriter c1, NetObjectWriter c2) {
        left = c1;
        right = c2;
    }

    /**
     * Called from the model when its state is changed
     *
     * @param aPongModel Model of game
     * @param arg        Arguments - not used
     */
    public synchronized void update(Observable aPongModel, Object arg) {
        DEBUG.trace("Updating clients");
        S_PongModel model = (S_PongModel) aPongModel;
        ball = model.getBall();
        bats = model.getBats();

        // Array to store the games state in
        GameObject[] state = new GameObject[3];

        // Player zero at position 0
        state[0] = bats[0];

        // Player one at position 1
        state[1] = bats[1];

        // ball at position 3
        state[2] = ball;

        // Update both players
        left.put(state);
        right.put(state);

    }

}

