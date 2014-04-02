package server;

import common.*;

import java.awt.event.KeyEvent;

/**
 * Individual player run as a separate thread to allow
 * updates to the model when a player moves there bat
 */
class Player implements Runnable {

    /**
     * The players ID so the correct timestamp can be extracted
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

            DEBUG.trace("Key Press Received from Player " + playerId);

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
