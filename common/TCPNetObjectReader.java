package common;

import java.io.*;
import java.net.Socket;

/**
 * Wrapper to allow reading of objects from a socket
 */
public class TCPNetObjectReader extends ObjectInputStream implements NetObjectReader {

    /**
     * Constructor
     *
     * @param s server initialised with server info
     * @throws IOException
     */
    public TCPNetObjectReader(Socket s) throws IOException {

        super(s.getInputStream());

    }

    /**
     * Waits for communication from the server
     *
     * @return the object sent by the server
     */
    public synchronized Object get() {

        try {

            return readObject();

        } catch (Exception err) {

            DEBUG.error("TCPNetObjectReader.get %s", err.getMessage());

            return null;

        }

    }

}


