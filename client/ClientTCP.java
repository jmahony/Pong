package client;

import common.Global;
import common.TCPNetObjectReader;
import common.TCPNetObjectWriter;

import java.io.Serializable;
import java.net.Socket;

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

        try {

            System.out.println("Attempting to make contact with: " +
                    Global.host + ":" + Global.port);

            Socket socket = new Socket(Global.host, Global.port);

            TCPNetObjectReader nor = new TCPNetObjectReader(socket);
            TCPNetObjectWriter now = new TCPNetObjectWriter(socket);

            // Receive the player id
            int playerId = (int) nor.get();

            // Setup the server, just send an empty array
            now.put(new Serializable[0]);

            Player player = new Player(model, nor, playerId);

            cont.addTCPWriter(now);

            player.start();

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());

        }

    }
}
