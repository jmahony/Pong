package client;

import common.Global;
import common.TCPNetObjectReader;
import common.TCPNetObjectWriter;

import java.io.Serializable;
import java.net.Socket;

/**
 * ClientTCP sets up/joins a game on the server. Moves and updates are
 * sent/received over TCP.
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

        try {

            System.out.println("Attempting to make contact with: " +
                    Global.host + ":" + Global.port);

            Socket socket = new Socket(Global.host, Global.port);

            TCPNetObjectReader nor = new TCPNetObjectReader(socket);
            TCPNetObjectWriter now = new TCPNetObjectWriter(socket);

            // Receive player id and port
            Serializable[] data = (Serializable[]) nor.get();
            int playerId = (int) data[0];

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
