package client;

import common.*;

import static common.Global.*;

import java.net.Socket;

/**
 * Start the client that will display the game for a player
 */
class Client {

    /**
     * The servers address
     */
    final String host = "localhost";

    /**
     * The servers port
     */
    final int port = 50000;

    public static void main(String args[]) {
        (new Client()).start();
    }

    /**
     * Start the Client
     */
    public void start() {
        DEBUG.set(false);
        DEBUG.trace("Pong Client");

        C_PongModel model = new C_PongModel();
        C_PongView view = new C_PongView();
        C_PongController cont = new C_PongController(model, view);

        makeContactWithServer(model, cont);

        model.addObserver(view);       // Add observer to the model
        view.setVisible(true);           // Display Screen
    }

    /**
     * Make contact with the Server who controls the game
     * Players will need to know about the model
     *
     * @param model Of the game
     * @param cont  Controller (MVC) of the Game
     */
    public void makeContactWithServer(C_PongModel model, C_PongController cont) {

        DEBUG.trace("Client");

        try {

            // Open a connection to the server
            Socket socket = new Socket(host, port);

            // Pass a reference to the socket to the controller so it can send key presses
            cont.addSocket(socket);

            Player player = new Player(model, socket);

            player.start();

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());

        }

    }
}
