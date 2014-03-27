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

        // Now need to send position of game objects to the client as the model on the server has changed

        Serializable[] leftState, rightState;

        leftState = new Serializable[] {
            bats[0],
            bats[1],
            ball,
            model.getPing(0)
        };

        rightState = new Serializable[] {
            bats[0],
            bats[1],
            ball,
            model.getPing(1)
        };

        (new Thread(new NetworkDelay(left, leftState, 100))).start();
        (new Thread(new NetworkDelay(right, rightState, 100))).start();

    }

}


class NetworkDelay implements Runnable {

    private NetObjectWriter now;
    private long delay = 0;
    private Object payload;

    public NetworkDelay(NetObjectWriter now, Object payload, long delay) {
        this.now = now;
        this.delay = delay;
        this.payload = payload;
    }

    @Override public void run() {

        try {

            Thread.sleep(delay);

            now.put(payload);

        } catch (InterruptedException e) {

            e.printStackTrace();

        }

    }
}
