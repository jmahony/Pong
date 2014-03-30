package common;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

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

    private MulticastSocket socket = null;
    private InetAddress group = null;
    private int port = 0;

    public NetMCWriter(int aPort, String mca) throws IOException {
        port = aPort;
        DEBUG.trace("NetMCWrite: port [%5d] MCA [%s]", port, mca);
        socket = new MulticastSocket(port);
        group = InetAddress.getByName(mca);
        socket.setTimeToLive(40);
    }

    public void close() throws IOException {
        socket.leaveGroup(group);
        socket.close();
    }

    @Override
    public synchronized boolean put(Object message) {
        DEBUG.trace("MCWrite: port [%5d] <%s>", port, message);

        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput oos = new ObjectOutputStream(bos);

            oos.writeObject(message);

            oos.close();

            byte[] buf = bos.toByteArray();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);

            socket.send(packet);

            return true;

        } catch (IOException e) {

            e.printStackTrace();

            return false;

        }

    }

    @Override
    public void put(Object data, long delay) {
        put(data);
    }


}
