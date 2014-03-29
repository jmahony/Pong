package server;

import common.*;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;
import common.Global;

/**
 * Displays a graphical view of the game of pong
 */
class S_PongView implements Observer {

    /**
     * The controller, this is never used.
     */
    private S_PongController pongController;

    /**
     * The games ball
     */
    private GameObject ball;

    /**
     * Both players bats
     */
    private GameObject[] bats;

    /**
     * Writer objects so updates can be sent to the clients
     */
    private NetObjectWriter left, right;

    /**
     * Takes in the net object writers for both players
     *
     * @param left the left players writer
     * @param right the right players writer
     */
    public S_PongView(NetObjectWriter left, NetObjectWriter right) {

        this.left  = left;
        this.right = right;

    }

    /**
     * Called from the model when its state is changed
     *
     * Create two arrays of serializable that contain the updates to be sent to
     * each player. The only difference between the two arrays are the
     * timestamps to calculate the RTT. It is necessary to have two array
     * because the states are sent asynchronously, therefore if we just change
     * the relevant index, both players would get the seconds players timestamp.
     *
     * @param aPongModel Model of game
     * @param arg        Arguments - not used
     */
    public synchronized void update(Observable aPongModel, Object arg) {

        DEBUG.trace("Updating clients");

        S_PongModel model = (S_PongModel) aPongModel;

        Serializable[] leftState, rightState;

        ball = model.getBall();
        bats = model.getBats();

        // The state the send to the left player
        leftState = new Serializable[] {
            bats[0],
            bats[1],
            ball,
            model.getLastPingTimestamp(Global.LEFT_PLAYER)
        };

        // The state to send to the right player
        rightState = new Serializable[] {
            bats[0],
            bats[1],
            ball,
            model.getLastPingTimestamp(Global.RIGHT_PLAYER)
        };

        if (Global.DELAY_COMPENSATION) {

            // Get both players average ping
            // e.g. leftAvgPing = 200
            //      rightAvgPing = 200
            long leftAvgPing  = model.getAveragePing(Global.LEFT_PLAYER),
                 rightAvgPing = model.getAveragePing(Global.RIGHT_PLAYER);

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
            long leftActualDelay =
                    leftDelay + model.getLastUpdateDelay(Global.LEFT_PLAYER),
                 rightActualDelay =
                    rightDelay + model.getLastUpdateDelay(Global.RIGHT_PLAYER);

            String leftActualDelayString = String.format("(%d - %d) + %d = %d", rightAvgPing, leftAvgPing, model.getLastUpdateDelay(Global.LEFT_PLAYER), leftActualDelay);
            String rightActualDelayString = String.format("(%d - %d) + %d = %d", leftAvgPing, rightAvgPing, model.getLastUpdateDelay(Global.RIGHT_PLAYER), rightActualDelay);
            System.out.println("--------------------------- TICK ---------------------------");

            System.out.println("----------- LEFT --------------");
            System.out.println("Calculating left delay");
            System.out.println("leftAvgPing: " + leftAvgPing);
            System.out.println("rightAvgPing: " + rightAvgPing);
            System.out.println("leftDelay: " + leftDelay);
            System.out.println("rightDelay: " + rightDelay);
            System.out.println("leftActualDelay: " + leftActualDelay);
            System.out.println("lastUpdateDelay: " + model.getLastUpdateDelay(Global.LEFT_PLAYER));
            System.out.println("Calculation of leftActualDelay: " + leftActualDelayString);

            System.out.println("----------- RIGHT --------------");
            System.out.println("Calculating right delay");
            System.out.println("leftAvgPing: " + leftAvgPing);
            System.out.println("rightAvgPing: " + rightAvgPing);
            System.out.println("leftDelay: " + leftDelay);
            System.out.println("rightDelay: " + rightDelay);
            System.out.println("rightActualDelay: " + rightActualDelay);
            System.out.println("lastUpdateDelay: " + model.getLastUpdateDelay(Global.RIGHT_PLAYER));
            System.out.println("Calculation of rightActualDelay: " + rightActualDelayString);

            // Send the update to both players with their corresponding delay
            left.put(leftState, leftActualDelay);
            right.put(rightState, rightActualDelay);

            // Store how much we've delayed each player so we
            // can offset it against their delay on the next tick
            model.setLastUpdateDelay(Global.LEFT_PLAYER, leftDelay);
            model.setLastUpdateDelay(Global.RIGHT_PLAYER, rightDelay);

        } else {

            left.put(leftState);
            right.put(rightState);

        }

    }

}
