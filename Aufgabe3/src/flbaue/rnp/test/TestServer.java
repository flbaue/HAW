package flbaue.rnp.test;

import flbaue.rnp.FCpacket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by florian on 10.12.14.
 */
public class TestServer {

    public final static int UDP_PACKET_SIZE = 1008;

    public static void main(String[] args) throws Exception {
        new TestServer().run();
    }

    private void run() throws Exception {
        DatagramSocket socket = new DatagramSocket(20000);
        byte[] receiveData = new byte[UDP_PACKET_SIZE];
        DatagramPacket datagramPacket = new DatagramPacket(receiveData, UDP_PACKET_SIZE);
        socket.receive(datagramPacket);
        System.out.println(datagramPacket);
        System.out.println("");
        FCpacket fcReceivePacket = new FCpacket(datagramPacket.getData(), datagramPacket.getLength());
        System.out.println(fcReceivePacket);
        System.out.println( new String(fcReceivePacket.getData(), "UTF-8"));
    }
}
