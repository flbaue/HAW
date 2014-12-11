package flbaue.rnp.test;

import flbaue.rnp.FCpacket;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by florian on 10.12.14.
 */
public class TestClient {

    public static void main(String[] args) throws Exception {
        new TestClient().run();
    }

    private void run() throws Exception {
        FCpacket packet = makeControlPacket();

        InetAddress serverAddress = InetAddress.getByName("localhost");
        int serverPort = 20000;

        DatagramPacket datagramPacket = new DatagramPacket(packet.getSeqNumBytesAndData(), packet.getLen() + 8, serverAddress, serverPort);
        DatagramSocket socket = new DatagramSocket();

        socket.send(datagramPacket);
        System.out.println(packet.getSeqNum());
        System.out.println(packet.getData());
        System.out.println(packet.getLen());
        System.out.println(packet.getSeqNumBytesAndData());
    }

    public FCpacket makeControlPacket() {
   /* Create first packet with seq num 0. Return value: FCPacket with
     (0 destPath ; windowSize ; errorRate) */
        String sendString = "/ABC/test/pfad" + ";" + "99" + ";" + "10";
        byte[] sendData = null;
        try {
            sendData = sendString.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new FCpacket(0, sendData, sendData.length);
    }
}
