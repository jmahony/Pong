package client;

import common.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

/**
 * ClientMC sets up/joins a game on the server. Moves are sent over TCP and all
 * updates are received via multicast UDP.
 */
class ClientMC extends Client {

    /**
     * Makes contact with the server by creating a socket, a handshake is
     * carried out to exchange information about how the server should setup the
     * game and the server will provide the players ID and the port to listen to.
     * Also the reader and writer objects are created to speak with the server.
     *
     * In this case "mc" is passed to the server to indicate that this will be
     * a multicast game.
     *
     * @param model Of the game
     * @param cont  Controller (MVC) of the Game
     */
    @Override
    public void makeContactWithServer(C_PongModel model, C_PongController cont) {

        System.out.println("Attempting to make contact with: " +
                Global.host + ":" + Global.port);

        try {

            // Create a new socket to communicate through
            Socket socket = new Socket(Global.host, Global.port);

            NetObjectReader nor    = new TCPNetObjectReader(socket);
            TCPNetObjectWriter now = new TCPNetObjectWriter(socket);

            // Tell the server this is a multicast game
            now.put(new Serializable[]{
                    "mc"
            });
            System.out.println("Sent setup info");

            System.out.println("Waiting for payload");
            // Receive player id and port
            Serializable[] data = (Serializable[]) nor.get();
            int playerId = (int) data[0];
            int port     = (int) data[1];

            System.out.println("Payload Received playedId: " + playerId + " port: " + port);

            NetObjectReader bnor = new NetMCReader(port, Global.MC_ADDRESS);

            Player player = new Player(model, bnor, playerId);

            // Add socket to controller so it can send moves tp the server
            cont.addTCPWriter(now);

            // We don't need the reader anymore
            nor = null;

            player.start();

        } catch (IOException e) {

            System.out.println("Could not connect to server");
            e.printStackTrace();

        }

    }

}
