package net.sourceforge.jicyshout.jicylib1;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class _UDPMetadataListener extends Thread {
    //    implements MP3MetadataParser{

    DatagramSocket datagramSocket;
    boolean active;

    public _UDPMetadataListener (String hostName, int port) 
        throws UnknownHostException, SocketException {
        super();
        InetAddress hostAddress = InetAddress.getByName (hostName);
        // datagramSocket = new DatagramSocket();
        // datagramSocket.connect (hostAddress, port);
        datagramSocket = new DatagramSocket (port);
        active = true;
        this.start();
        System.out.println ("Created UDPMetadataListener, host " +
                            datagramSocket.getInetAddress() +
                            ", port " +  datagramSocket.getPort() );
    }


    /* comment here about the problems caused by deprecating
       Thread.stop() -- we can't bail on the blocking
       DatagramSocket.receive().  Perhaps one fix is the
       non-blocking IO of java 1.4?
     */

    public void run() {
        byte[] buffy = new byte[1024];
        DatagramPacket packet =
            new DatagramPacket (buffy, buffy.length);
        while (active) {
            try {
                datagramSocket.setReceiveBufferSize (buffy.length);
                datagramSocket.setSoTimeout (2000);
                try {
                    System.out.println ("udp - about to receive");
                    datagramSocket.receive (packet); // blocks
                    System.out.println ("udp - received datagram: " +
                                        new String (buffy,0, packet.getLength()));
                } catch (InterruptedIOException iioe) {
                    if (iioe.bytesTransferred > 0) {
                        System.out.println ("interrupted with " +
                                            iioe.bytesTransferred +
                                            " bytes received");
                    }
                }
                // TODO: any need to sleep here?
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        System.out.println ("udp listener thread no longer active");
    }

    /** tell the UDPMetadataListener to stop listening as soon
        as he unblocks from the next message.
     */
    public void setActive (boolean a) {
        this.active = a;
    }

}
