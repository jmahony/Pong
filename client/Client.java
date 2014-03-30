package client;

import common.*;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

/**
 * Start the client that will display the game for a player
 */
abstract class Client {

    public static void main(String args[]) throws ParseException {

        Options options = new Options() {{
            addOption("m",   false, "Whether to multicast the game");
            addOption("p",   false, "Set the server port");
            addOption(OptionBuilder.
                withArgName("port").
                hasArg().
                withDescription("If you want to spectate").create("s"));
            addOption("h",   true,  "The hostname of the server");
            addOption("ddc", false, "disable delay compensation");
        }};

        CommandLineParser parser = new GnuParser();
        CommandLine cmd = parser.parse(options, args);

        // If the port option is supplied, set the port
        if (cmd.hasOption("p")) {
            Global.port = Integer.parseInt(cmd.getOptionValue("p"), 10);
        }

        // If the host option is set, set the host
        if (cmd.hasOption("h")) {
            Global.host = cmd.getOptionValue("h");
        }

        // If disable delay compensation is set, disable dc
        if (cmd.hasOption("ddc")) {
            Global.delay_compensation = false;
        }

        // If multicast is set, create a multicast client
        // else create a TCP client
        if (cmd.hasOption("m")) {

            // Can't do multicast and delay compensation
            Global.delay_compensation = false;

            (new ClientMultiCast()).start();

        // If option s, go into spectate mode and watch on the supplied port
        } else if(cmd.hasOption("s")) {

            String port = cmd.getOptionValue("s");

            (new ClientSpectator(Integer.parseInt(port))).start();

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

class ClientMultiCast extends Client {

    @Override
    public void makeContactWithServer(C_PongModel model, C_PongController cont) {

        System.out.println("Attempting to make contact with: " +
                Global.host + ":" + Global.port);

        try {

            // Create a new socket to communicate through
            Socket socket = new Socket(Global.host, Global.port);

            NetObjectReader nor    = new TCPNetObjectReader(socket);
            TCPNetObjectWriter now = new TCPNetObjectWriter(socket);

            // Receive player id and port
            Serializable[] data = (Serializable[]) nor.get();
            int playerId = (int) data[0];
            int port     = (int) data[1];

            // Tell the server this is a multicast game
            now.put(new Serializable[]{
                    "mc"
            });

            NetObjectReader bnor = new NetMCReader(port, Global.MULTI_CAST_ADDRESS);

            Player player = new Player(model, bnor, playerId);

            // Add the socket to the controller so it can send moves to the
            // server
            cont.addTCPWriter(now);

            nor = null;

            player.start();

        } catch (IOException e) {

            System.out.println("Could not connect to server");
            e.printStackTrace();

        }

    }

}

/**
 * ClientSpectator simply observes a game on the given port, no connections
 * are made to the server
 */
class ClientSpectator extends Client {

    /**
     * The port the server will be broadcasting the game on
     */
    private final int gameMCPort;

    /**
     * Takes in the port to listen to
     *
     * @param gameMCPort
     */
    public ClientSpectator(int gameMCPort) {

        this.gameMCPort = gameMCPort;

    }

    /**
     * Start the Client
     * Create the pong model, view and controller and initiates.
     */
    public void start() {

        DEBUG.set(false);

        C_PongModel model = new C_PongModel();
        C_PongView view = new C_PongView();
        C_PongController cont = new C_PongController(model, view, true);

        makeContactWithServer(model, cont);

        model.addObserver(view);       // Add observer to the model
        view.setVisible(true);           // Display Screen

    }

    /**
     * This doesn't actually make any connections to the server, it simply sets
     * up a multicast reader to listen or a certain port
     *
     * @param model Of the game
     * @param cont  Controller (MVC) of the Game
     */
    @Override
    public void makeContactWithServer(C_PongModel model, C_PongController cont) {

        System.out.println("Attempting to spectate: " +
                Global.host + ":" + gameMCPort);

        try {

            NetObjectReader bnor = new NetMCReader(gameMCPort, Global.MULTI_CAST_ADDRESS);

            Player player = new Player(model, bnor, Global.SPECTATOR);

            player.start();

        } catch (IOException e) {

            System.out.println("Could not connect to server");

            e.printStackTrace();

        }

    }

}

