package server;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.*;

/**
 * Start the game server
 * The call to makeActiveObject() in the model
 * starts the play of the game
 */
class Server {

    /**
     * The number of players connected to the server.
     * This is used to calculate the players number.
     */
    private int playerNo = 0;

    /**
     * The amount of games being played on the server
     */
    private int gamesNo = 0;

    /**
     * The broadcast port, used when updates are sent via multicast
     */
    private int broadcastPort = 50002;

    public static void main(String args[]) {
        (new Server()).start();
    }

    /**
     * Start the server
     */
    public void start() {
        DEBUG.set(false);
        DEBUG.trace("Pong Server");
        //DEBUG.set(false);

        makeContactWithClients();

    }

    /**
     * Make contact with the clients who wish to play
     * Players will need to know about the model
     */
    public void makeContactWithClients() {

        try {

            ExecutorService es = Executors.newFixedThreadPool(Global.MAX_PLAYERS);

            ServerSocket ss = new ServerSocket(Global.port);

            while (true) {

                S_PongModel model = new S_PongModel();

                // Setup left player
                Player playerLeft = clientHandshake(model, ss, Global.LEFT_PLAYER);

                es.execute(playerLeft);

                // Setup right player
                Player playerRight = clientHandshake(model, ss, Global.RIGHT_PLAYER);

                es.execute(playerRight);

                // Setup view
                S_PongView view = new S_PongView(
                        playerLeft.getWriter(),
                        playerRight.getWriter()
                );

                new S_PongController(model, view);

                model.addObserver(view); // Add observer to the model

                model.makeActiveObject(); // Start play

                System.out.println("Game no " + gamesNo++ + " created on port " + Global.port);

                broadcastPort++;

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    /**
     * Handshakes with the client, sending / receiving any setup info
     *
     * @param model the pong model
     * @param ss the servers socket
     * @param playerId the players id
     * @return the Runnable player
     * @throws IOException
     */
    private synchronized Player clientHandshake(S_PongModel model,
                            ServerSocket ss, int playerId) throws IOException {

        Socket socketLeft = ss.accept();

        System.out.println("Player " + playerNo++ + " has connected");

        NetObjectWriter now    = new TCPNetObjectWriter(socketLeft);
        TCPNetObjectReader nor = new TCPNetObjectReader(socketLeft);

        // Send the player id and gameNo back
        now.put(new Serializable[] {
                playerId,
                broadcastPort
        });

        // Wait for setup info
        Serializable[] setup = (Serializable[]) nor.get();

        // The first player to join gets to setup the game.
        if (playerId == 0) {

            if (setup.length > 0) {

                if (setup[0].equals("mc")) {

                    // Can't have delay compensation and multicast
                    Global.delay_compensation = false;

                    now = new NetMCWriter(broadcastPort, Global.MC_ADDRESS);
                    System.out.println("Broadcasting on port " + Global.MC_ADDRESS + ":" + broadcastPort);

                }

            }

        }

        return new Player(playerId, model, nor, now);

    }

}

// TODO: Move player into its own file
/**
 * Individual player run as a separate thread to allow
 * updates to the model when a player moves there bat
 */
class Player implements Runnable {

    /**
     * The players ID
     */
    private final int playerId;

    /**
     * A reference to the pong model
     */
    private S_PongModel pongModel;

    /**
     * Used to receive communication from the server
     */
    private NetObjectReader nor;

    /**
     * Used to send communications to the server
     */
    private NetObjectWriter now;

    /**
     * Constructor
     *
     * @param player Player 0 or 1
     * @param model  Model of the game
     * @param nor    NetObjectReader to receive moves
     */
    public Player(int player, S_PongModel model, NetObjectReader nor, NetObjectWriter now) {

        playerId = player;
        pongModel = model;
        this.nor = nor;
        this.now = now;

    }

    /**
     * Gets the players reader
     *
     * @return the players reader
     */
    public NetObjectReader getReader() {

        return this.nor;

    }

    /**
     * Gets the players writer
     *
     * @return the players writer
     */
    public NetObjectWriter getWriter() {

        return this.now;

    }

    /**
     * Get and update the model with the latest bat movement
     */
    public void run() {

        while (true) {

            // Wait for a message from the player
            Object o = nor.get();

            if ( o == null ) break;

            String message = (String) o;

            String[] messages = message.split(":");

            // Which key they pressed
            int keypress  = Integer.parseInt(messages[0], 10);

            // A timestamp when they sent the request, this will be sent back
            // on the next tick so the client can calculate round trip time
            long timestamp = Long.parseLong(messages[1], 10);

            // The average ping of the player
            long averagePing  = Long.parseLong(messages[2], 10);

            // The round trip time of the last request
            long roundTripTime = Long.parseLong(messages[3], 10);

            System.out.println("Key Press Received from Player " + playerId);

            GameObject bat = pongModel.getBats()[playerId];
            pongModel.setLastPingTimestamp(playerId, timestamp);
            pongModel.setAveragePing(playerId, averagePing);
            pongModel.setLastRequestRTT(playerId, roundTripTime);

            // TODO: Fix this, any key than up moves the bat down, Its not wrong though!
            bat.moveY(-KeyEvent.VK_UP == keypress ? -Global.BAT_MOVE : Global.BAT_MOVE);

            pongModel.setBat(playerId, bat);

            pongModel.modelChanged();

        }

    }

}
