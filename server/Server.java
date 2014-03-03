package server;

import java.io.IOException;
import java.net.*;

import common.*;

/**
 * Start the game server
 * The call to makeActiveObject() in the model
 * starts the play of the game
 */
class Server {
    private NetObjectWriter p0, p1;
    final int port = 50000;

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
        S_PongModel model = new S_PongModel();

        makeContactWithClients(model);

        S_PongView view = new S_PongView(p0, p1);
        new S_PongController(model, view);

        model.addObserver(view); // Add observer to the model
        model.makeActiveObject(); // Start play
    }

    /**
     * Make contact with the clients who wish to play
     * Players will need to know about the model
     *
     * @param model Of the game
     */
    public void makeContactWithClients(S_PongModel model) {

        try {

            ServerSocket ss = new ServerSocket(port);

            Socket s0 = ss.accept();

            p0 = new NetObjectWriter(s0);

            Player playerOne = new Player(0, model, s0);

            playerOne.start();

            Socket s1 = ss.accept();

            p1 = new NetObjectWriter(s1);

            Player playerTwo = new Player(1, model, s1);

            playerTwo.start();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }
}

/**
 * Individual player run as a separate thread to allow
 * updates to the model when a player moves there bat
 */
class Player extends Thread {

    final int playerId;

    private S_PongModel pongModel;

    private Socket socket;

    private int i = 0;


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

                System.out.println("Key Press Received from Player " + playerId);

                GameObject bat = pongModel.getBats()[playerId];

                bat.moveY(message.equals("u") ? -10 : 10);

                pongModel.setBat(playerId, bat);

                pongModel.modelChanged();

            }


        } catch (IOException e) {

            e.printStackTrace();

        }

    }
}
