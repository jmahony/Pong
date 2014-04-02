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
     * This method performs a handshake with the server to receive information
     * needed by the client, this includes the port to listen on (not actually
     * used by ClientTCP) and their player ID, this is needed in order for the
     * timestamp to be extracted from the servers payload
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

            // Setup the server, just send an empty array
            now.put(new Serializable[0]);

            System.out.println("Sent setup info");

            System.out.println("Waiting for payload");

            // Receive player id and port
            Serializable[] data = (Serializable[]) nor.get();
            int playerId = (int) data[0];

            System.out.println("Payload Received playedId: " + playerId);

            Player player = new Player(model, nor, playerId);

            cont.addTCPWriter(now);

            player.start();

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());

        }

    }

}
