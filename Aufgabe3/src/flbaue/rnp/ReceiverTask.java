package flbaue.rnp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by florian on 06.12.14.
 */
public class ReceiverTask implements Runnable {

    private final DatagramSocket socket;
    private final SendBuffer sendBuffer;
    private final InetAddress serverAddress;
    private final int serverPort;

    public ReceiverTask(DatagramSocket socket, SendBuffer sendBuffer, InetAddress serverAddress, int serverPort) {
        this.socket = socket;
        this.sendBuffer = sendBuffer;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                byte[] bytes = new byte[FileCopyClient.UDP_PACKET_SIZE];
                DatagramPacket datagramPacket = new DatagramPacket(bytes, FileCopyClient.UDP_PACKET_SIZE);
                try {
                    socket.receive(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (serverAddress.equals(datagramPacket.getAddress()) && serverPort == datagramPacket.getPort()) {

                    FCpacket packet = new FCpacket(datagramPacket.getData(), datagramPacket.getLength());
                    System.out.println("Receiver: Response for packet " + packet.getSeqNum());
                    sendBuffer.acknowledgePackage(packet);
                }
            } catch (Exception e) {
                if(e instanceof InterruptedException){
                    Thread.currentThread().interrupt();
                } else {
                    e.printStackTrace();
                }
            }
        }
    }
}
