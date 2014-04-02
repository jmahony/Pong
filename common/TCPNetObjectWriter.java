package common;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Wrapper for reading an object from a socket
 */
public class TCPNetObjectWriter extends ObjectOutputStream implements NetObjectWriter {

    /**
     * Constructor
     *
     * @param s
     * @throws IOException
     */
    public TCPNetObjectWriter(Socket s) throws IOException {

        super(s.getOutputStream());

        s.setTcpNoDelay(true);

    }

    /**
     * Send data to server
     *
     * @param data
     * @return success of communication
     */
    public synchronized boolean put(Object data) {

        try {

            // Reset so we don't send the same data
            reset();

            // Write object to output stream
            writeObject(data);

            // Send data
            flush();

            return true;

        } catch (IOException err) {

            DEBUG.error("TCPNetObjectWriter.get %s", err.getMessage());

            return false;                           // Failed write

        }

    }

    /**
     * Sends a delay message to the server. A new thread is spawned, and
     * instantly run and the slept for the duration of the delay. When the
     * thread wakes, the data will be sent.
     *
     * @param data the communication payload
     * @param delay how long to delay the communication
     */
    public void put(final Object data, final long delay) {

        // If the delay is less than 1, just send it instantly, a negative
        // delay does not make sense (send it in the past?)
        if (delay < 1) {

            put(data);

        } else {

            // Create a thread, sleep and send data
            (new Thread(new Runnable() {
                @Override public void run() {

                    try {

                        Thread.sleep(delay);

                        TCPNetObjectWriter.this.put(data);

                    } catch (InterruptedException e) {

                        e.printStackTrace();

                    }
                }
            })).start();

        }

    }

}
