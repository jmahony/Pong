package server;

import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.*;
import org.apache.commons.cli.*;

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

    public static void main(String args[]) throws ParseException {

        // Setup and parse options
        CommandLineParser parser = new GnuParser();
        CommandLine cmd = parser.parse(getOptions(), args);

        // If the port option is supplied, set the port
        if (cmd.hasOption("p")) {

            Global.port = Integer.parseInt(cmd.getOptionValue("p"), 10);

        }

        (new Server()).start();
    }

    /**
     * Start the server
     */
    public void start() {

        //TODO: Accept arguments for hostname and port
        DEBUG.set(false);
        DEBUG.trace("Pong Server");
        //DEBUG.set(false);

        System.out.println("Starting Server");
        System.out.println("Listening on port " + Global.port);

        makeContactWithClients();

    }

    /**
     * Setup command line options
     *
     * @return options
     */
    @SuppressWarnings("static-access")
    private static Options getOptions() {

        Options options = new Options() {{
            addOption("m",   false, "Whether to multicast the game");
            addOption("p",   false, "Set the server port");
            addOption(OptionBuilder.
                    withArgName("port").
                    hasArg().
                    withDescription("If you want to spectate").
                    create("p"));

            addOption(OptionBuilder.
                    withArgName("port").
                    hasArg().
                    withDescription("If you want to spectate").
                    create("s"));

            addOption(OptionBuilder.
                    withArgName("host").
                    hasArg().
                    withDescription("The hostname of the server").
                    create("h"));

            addOption("ddc", false, "Disable delay compensation");
        }};

        return options;

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

        // Wait for setup info
        Serializable[] setup = (Serializable[]) nor.get();

        // Send the clients setup info
        now.put(new Serializable[] {
            playerId,
            broadcastPort
        });

        // The first player to join gets to setup the game.
        if (playerId == 0) {

            if (setup.length > 0) {

                if (setup[0].equals("mc")) {

                    // Can't have delay compensation and multicast
                    model.setDelayCompensation(false);
                    model.setIsMultiCast(true);
                    now = new NetMCWriter(broadcastPort, Global.MC_ADDRESS);
                    System.out.println("Broadcasting on port " + Global.MC_ADDRESS + ":" + broadcastPort);

                }

            }

        }

        return new Player(playerId, model, nor, now);

    }

}

// TODO: Move player into its own file

