package client;

import common.*;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.Socket;

import common.GameObject;

/**
 * Pong controller, handles user interactions
 */
public class C_PongController {

    /**
     * The pong model
     */
    private C_PongModel model;

    /**
     * The pong view
     */
    private C_PongView view;

    /**
     * The socket to the server
     */
    private Socket socket;

    /**
     * NetObjectWriter to send message to the server
     */
    private NetObjectWriter now;

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

            // If we haven't already initialised the net object writer, initialise it
            if (now == null) {

                now = new NetObjectWriter(socket);

            }

            String action = null;

            switch (keyCode) {
                case -KeyEvent.VK_UP:
                    action = "u";
                    break;
                case -KeyEvent.VK_DOWN:
                    action = "d";
                    break;
            }

            DEBUG.trace("Key Pressed");

            // Send the action to the server
            now.put(action);

        } catch (IOException e) {

            System.out.println("Error: " + e.getMessage());

        }

    }

}

