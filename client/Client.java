package client;

import common.*;

import static common.Global.*;

import java.net.Socket;

/**
 * Start the client that will display the game for a player
 */
abstract class Client {

    public static void main(String args[]) {

        if (args.length > 0 && args[0] == "1") {

            (new ClientMultiCast()).start();

        } else {

            (new ClientTCP()).start();

        }

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
    public abstract void makeContactWithServer(C_PongModel model, C_PongController cont);

}

/**
 * Start the client that will display the game for a player
 */
class ClientTCP extends Client {

    /**
     * Make contact with the Server who controls the game
     * Players will need to know about the model
     *
     * @param model Of the game
     * @param cont  Controller (MVC) of the Game
     */
    @Override
    public void makeContactWithServer(C_PongModel model, C_PongController cont) {
        // Also starts the Player task that get the current state
        //  of the game from the server

        System.out.println("Client");

        try {

            Socket socket = new Socket(Global.HOST, Global.PORT);

            cont.addSocket(socket);

            Player player = new Player(model, socket);

            player.start();

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());

        }

    }
}

class ClientMultiCast extends Client {

    @Override
    public void makeContactWithServer(C_PongModel model, C_PongController cont) {

    }

}
