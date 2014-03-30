package common;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Multicast reader
 */
public class NetMCReader implements NetObjectReader {

    /**
     * The multicast socket
     */
    private MulticastSocket socket = null;

    /**
     * The multicast address to listen on
     */
    private InetAddress address = null;

    /**
     * The port to listen on
     */
    private int port = 0;

    /**
     * Constructor
     *
     * @param port the port to listen on
     * @param address the multicast address to listen on
     * @throws IOException
     */
    public NetMCReader(int port, String address) throws IOException {

        DEBUG.trace("MCRead: C port [%s] MCA [%s]", port, address);

        this.port = port;
        socket = new MulticastSocket(port);
        this.address = InetAddress.getByName(address);
        socket.joinGroup(this.address);

    }

    /**
     * Stops listening to the multicast broadcast
     *
     * @throws IOException
     */
    public void close() throws IOException {

        socket.leaveGroup(address);
        socket.close();

    }


    /**
     * Waits for a message from the server
     *
     * @return the message from the server
     */
    public synchronized Object get() {

        DEBUG.trace("MCRead: on port [%d]", port);

        byte[] buf = new byte[512];

        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        ObjectInput in = null;

        try {

            socket.receive(packet);

            InputStream bos = new ByteArrayInputStream(buf);

            in = new ObjectInputStream(bos);

            return in.readObject();

        } catch (IOException e) {

            e.printStackTrace();

        } catch (ClassNotFoundException e) {

            e.printStackTrace();

        }

        return null;

    }

}
