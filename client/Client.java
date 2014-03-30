package client;

import common.*;
import org.apache.commons.cli.*;

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

            (new ClientMC()).start();

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
