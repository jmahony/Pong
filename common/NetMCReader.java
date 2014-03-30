package common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Multicast reader
 */
public class NetMCReader implements NetObjectReader {

    private MulticastSocket socket = null;
    private InetAddress group = null;
    private int port = 0;

    public NetMCReader(int aPort, String mca) throws IOException {
        port = aPort;
        DEBUG.trace("MCRead: C port [%s] MCA [%s]", port, mca);
        socket = new MulticastSocket(port);
        group = InetAddress.getByName(mca);
        socket.joinGroup(group);
    }

    public void close() throws IOException {
        socket.leaveGroup(group);
        socket.close();
    }


    public synchronized String get() {
        DEBUG.trace("MCRead: on port [%d]", port);
        byte[] buf = new byte[512];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String m = new String(packet.getData(), 0, packet.getLength());
        DEBUG.trace("MCRead: Read <%s>", m);
        return m;
    }
}
