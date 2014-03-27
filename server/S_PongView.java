package server;

import common.*;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;


/**
 * Displays a graphical view of the game of pong
 */
class S_PongView implements Observer {
    private S_PongController pongController;
    private GameObject ball;
    private GameObject[] bats;
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

        Serializable[] state = new Serializable[4];

        state[0] = bats[0];
        state[1] = bats[1];
        state[2] = ball;

        // Now need to send position of game objects to the client as the model on the server has changed

        // Check if set need to update the player to delay
        if ((model.delayAmount == -1 && model.playerToDelay == -1)) {
            model.setPlayerToDelay();
        }

        // See if we're allowed to update player 0
        if (model.playerToDelay != 0 || model.playerToDelay == 0 && model.delayAmount == -1) {
            state[3] = model.getPing(0);
            left.put(state);
        }

        // See if we're allowed to update player 1
        if (model.playerToDelay != 1 || model.playerToDelay == 1 && model.delayAmount == -1) {
            state[3] = model.getPing(1);
            right.put(state);
        }

        // If the delay for the delayed player has not concluded, decrement the delay amount
        if (model.delayAmount > -1) {
            model.delayAmount--;
        } else { // Else reset the delay amount
            model.playerToDelay = -1;
        }

    }

}

