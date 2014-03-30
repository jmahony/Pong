package common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Multicast writer
 * IP Range 224.0.0.0 to 239.255.255.255
 */
public class NetMCWriter implements NetObjectWriter {

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
     * @param port
     * @param address
     * @throws IOException
     */
    public NetMCWriter(int port, String address) throws IOException {

        this.port = port;
        DEBUG.trace("NetMCWrite: port [%5d] MCA [%s]", port, address);
        socket = new MulticastSocket(port);
        this.address = InetAddress.getByName(address);
        socket.setTimeToLive(40);

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
     * Sends an serialised object to the client
     *
     * @param message the object to send
     * @return whether or not the message was sent
     */
    @Override
    public synchronized boolean put(Object message) {
        DEBUG.trace("MCWrite: port [%5d] <%s>", port, message);

        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            ObjectOutput oos = new ObjectOutputStream(bos);

            oos.writeObject(message);

            oos.close();

            byte[] buf = bos.toByteArray();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);

            socket.send(packet);

            return true;

        } catch (IOException e) {

            e.printStackTrace();

            return false;

        }

    }

    /**
     * Just call put, it doesn't make sense to delay multicast broadcasts
     *
     * @param data the data to send
     * @param delay - does nothing
     */
    @Override
    public void put(Object data, long delay) {

        put(data);

    }


}
