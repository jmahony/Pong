package client;

import common.*;

import java.io.IOException;
import java.net.Socket;

/**
 * Pong controller, handles user interactions
 */
public class C_PongController {
    private C_PongModel model;
    private C_PongView view;
    private Socket socket;
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
     * Add the socket to the controller so we can send moves
     *
     * @param s The players socket
     */
    public void addSocket(Socket s) {

        socket = s;

    }

    /**
     * Decide what to do for each key pressed
     *
     * @param keyCode The keycode of the key pressed
     */
    public void userKeyInteraction(int keyCode) {
        // Key typed includes specials, -ve
        // Char is ASCII value

        try {

            if (now == null) {

                now = new TCPNetObjectWriter(socket);

            }

            DEBUG.trace("Key Pressed");

            now.put(keyCode + ":" +
                    System.currentTimeMillis() + ":" +
                    model.getAveragePing() + ":" +
                    model.getLastRequestRTT());

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}

