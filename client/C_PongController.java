package client;

import common.*;

import java.io.IOException;
import java.net.Socket;

/**
 * Pong controller, handles user interactions and sends them to the server
 */
public class C_PongController {

    /**
     * Whether or not the client is a spectator. So we know whether to send
     * moves to the server or not.
     */
    private boolean spectator = false;

    /**
     * The games model
     */
    private C_PongModel model;

    /**
     * The games view
     */
    private C_PongView view;

    /**
     * Used to send moves to the server
     */
    private TCPNetObjectWriter now;

    /**
     * Constructor
     *
     * @param aPongModel Model of game on client
     * @param aPongView  View of game on client
     */
    public C_PongController(C_PongModel aPongModel, C_PongView aPongView) {

        model = aPongModel;
        view = aPongView;
        view.setPongController(this);  // View talks to controller

    }

    /**
     * Constructor
     *
     * @param aPongModel Model of game on client
     * @param aPongView  View of game on client
     */
    public C_PongController(C_PongModel aPongModel, C_PongView aPongView, boolean spectator) {
        model = aPongModel;
        view = aPongView;
        this.spectator = spectator;
        view.setPongController(this);  // View talks to controller
    }


    /**
     * Add the writer to the controller so we can send moves
     *
     * @param now The players socket
     */
    public void addTCPWriter(TCPNetObjectWriter now) {

        this.now = now;

    }

    /**
     * The the keycode to the server along with the current timestamp,
     * the average ping and the last request round tripp time.
     *
     * @param keyCode The keycode of the key pressed
     */
    public void userKeyInteraction(int keyCode) {
        // Key typed includes specials, -ve
        // Char is ASCII value

        DEBUG.trace("Key Pressed");

        // Ignore the key press its a spectator
        if (!spectator) {

            now.put(keyCode + Global.DELIMITER +
                    System.currentTimeMillis() + Global.DELIMITER +
                    model.getAveragePing() + Global.DELIMITER +
                    model.getLastRequestRTT());

        }

    }

}

