package client;

import common.DEBUG;
import common.Global;
import common.NetMCReader;

import java.io.IOException;

/**
 * ClientSpectator simply observes a game on the given port, no connections
 * are made to the server.
 */
class ClientSpectator extends Client {

    /**
     * The port the server will be broadcasting the game on
     */
    private final int gameMCPort;

    /**
     * Constructor
     *
     * @param gameMCPort
     */
    public ClientSpectator(int gameMCPort) {

        this.gameMCPort = gameMCPort;

    }

    /**
     * Start the Client
     */
    @Override
    public void start() {
        DEBUG.set(false);
        DEBUG.trace("Pong Client");

        C_PongModel model = new C_PongModel();
        C_PongView view = new C_PongView();
        C_PongController cont = new C_PongController(model, view, true);

        makeContactWithServer(model, cont);

        model.addObserver(view);
        view.setVisible(true);
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

            NetMCReader bnor = new NetMCReader(gameMCPort, Global.MC_ADDRESS);

            Spectator spectator = new Spectator(model, bnor);

            spectator.start();

        } catch (IOException e) {

            System.out.println("Could not connect to server");

            e.printStackTrace();

        }

    }

}
