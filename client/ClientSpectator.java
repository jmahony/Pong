package client;

import common.DEBUG;
import common.Global;
import common.NetMCReader;
import common.NetObjectReader;

import java.io.IOException;

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

            NetObjectReader bnor = new NetMCReader(gameMCPort, Global.MC_ADDRESS);

            Player player = new Player(model, bnor, Global.SPECTATOR);

            player.start();

        } catch (IOException e) {

            System.out.println("Could not connect to server");

            e.printStackTrace();

        }

    }

}

