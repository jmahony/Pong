package server;

import java.awt.event.KeyEvent;
import java.io.IOException;
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

            ServerSocket ss = new ServerSocket(Global.PORT);

            while (true) {

                S_PongModel model = new S_PongModel();

                Socket socketLeft = ss.accept();

                System.out.println("Player " + playerNo + " has connected");

                Runnable playerLeft = new Player(playerNo++ % 2, model, socketLeft);

                NetObjectWriter nowPlayerLeft = new NetObjectWriter(socketLeft);

                es.execute(playerLeft);

                Socket socketRight = ss.accept();

                System.out.println("Player " + playerNo + " has connected");

                Runnable playerRight = new Player(playerNo++ % 2, model, socketRight);

                NetObjectWriter nowPlayerRight = new NetObjectWriter(socketRight);

                es.execute(playerRight);

                S_PongView view = new S_PongView(nowPlayerLeft, nowPlayerRight);

                new S_PongController(model, view);

                model.addObserver(view); // Add observer to the model

                model.makeActiveObject(); // Start play

                System.out.println("Game no " + gamesNo++ + " created");

            }

        } catch (IOException e) {

            e.printStackTrace();

        }
    }
}

/**
 * Individual player run as a separate thread to allow
 * updates to the model when a player moves there bat
 */
class Player implements Runnable {

    final int playerId;

    private S_PongModel pongModel;

    private Socket socket;

    /**
     * Constructor
     *
     * @param player Player 0 or 1
     * @param model  Model of the game
     * @param s      Socket used to communicate the players bat move
     */
    public Player(int player, S_PongModel model, Socket s) {

        playerId = player;
        pongModel = model;
        socket = s;

    }

    /**
     * Get and update the model with the latest bat movement
     */
    public void run() {

        try {

            NetObjectReader nor = new NetObjectReader(socket);

            while (true) {

                Object o = nor.get();

                if ( o == null ) break;

                String message = (String) o;

                String[] messages = message.split(":");

                int keypress   = Integer.parseInt(messages[0], 10);
                long timestamp = Long.parseLong(messages[1], 10);
                long avgPing   = Long.parseLong(messages[2], 10);

                System.out.println("Key Press Received from Player " + playerId);

                GameObject bat = pongModel.getBats()[playerId];
                pongModel.setPing(playerId, timestamp);
                pongModel.setAvgPing(playerId, avgPing);

                bat.moveY(-KeyEvent.VK_UP == keypress ? -10 : 10);

                pongModel.setBat(playerId, bat);

                pongModel.modelChanged();

            }


        } catch (IOException e) {

            e.printStackTrace();

        }

    }
}
