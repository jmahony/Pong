package common;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Wrapper for reading an object from a socket
 */

public class TCPNetObjectWriter extends ObjectOutputStream implements NetObjectWriter {
    public TCPNetObjectWriter(Socket s) throws IOException {
        super(s.getOutputStream());
        s.setTcpNoDelay(true);       // Send data immediately
    }

    // write object to socket returning false on error
    public synchronized boolean put(Object data) {
        try {
            reset();
            writeObject(data);       // Write object
            flush();                   // Flush
            return true;               // Ok
        } catch (IOException err) {
            DEBUG.error("TCPNetObjectWriter.get %s",
                    err.getMessage());
            return false;                           // Failed write
        }
    }

    public void put(final Object data, final long delay) {

        if (delay < 1) {

            put(data);

        } else {

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
