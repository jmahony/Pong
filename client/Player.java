package client;

import common.*;

import java.io.IOException;
import java.net.Socket;

/**
 * Individual player run as a separate thread to allow
 * updates immediately the bat is moved
 */
class Player extends Thread {

    /**
     * The pong games model
     */
    private C_PongModel model;

    /**
     * The socket to the server
     */
    private Socket socket;

    /**
     * Constructor
     *
     * @param m - model of the game
     * @param s     - Socket used to communicate with server
     */
    public Player(C_PongModel m, Socket s) {
        model = m;
        socket = s;
    }

    /**
     * Get and update the model with the latest bat movement
     * sent by the server
     */
    public void run() {

        DEBUG.trace("Player.run");

        try {

            NetObjectReader nor = new NetObjectReader(socket);

            while (true) {

                // Wait for the server to send the games state
                // We will be getting a serialised array of GameObjects from the server
                // 0 => player 0 bat
                // 1 => player 1 bat
                // 2 => ball
                GameObject[] state = (GameObject[]) nor.get();

                // Update both players bat positions
                model.setBats(new GameObject[]{
                    state[0],
                    state[1]
                });

                // Update the ball position
                model.setBall(state[2]);

                // Tell the model its changed
                model.modelChanged();

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

    }
}
