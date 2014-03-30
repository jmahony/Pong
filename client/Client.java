package client;

import common.*;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.Socket;

/**
 * Start the client that will display the game for a player
 */
abstract class Client {

    public static void main(String args[]) throws ParseException {

        Options options = new Options() {{
            addOption("m",   false, "Whether to multicast the game");
            addOption("p",   false, "Set the port");
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

            NetObjectReader nor = new TCPNetObjectReader(socket);
            TCPNetObjectWriter now = new TCPNetObjectWriter(socket);

            cont.addTCPWriter(now);

            Player player = new Player(model, nor, (int) nor.get());

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

            now.put("mc");

            NetObjectReader bnor = new NetMCReader(Global.port, Global.MULTI_CAST_ADDRESS);

            // Add the socket to the controller so it can send moves to the
            // server
            cont.addTCPWriter(now);

            // Create a player to listen to game updates
            Player player = new Player(model, bnor, (int) nor.get());

            nor = null;

            player.start();

        } catch (IOException e) {

            System.out.println("Could not connect to server");
            e.printStackTrace();

        }

    }

}
