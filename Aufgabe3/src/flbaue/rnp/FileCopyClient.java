package flbaue.rnp;

/* FileCopyClient.java
 Version 0.1 - Muss erg�nzt werden!!
 Praktikum 3 Rechnernetze BAI4 HAW Hamburg
 Autoren:
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FileCopyClient extends Thread {

    // -------- Constants
    public final static boolean TEST_OUTPUT_MODE = false;
    public final static int UDP_PACKET_SIZE = 1008;
    public final int SERVER_PORT = 23000;
    public final int CLIENT_PORT = 23001;
    // -------- Public parms
    public String servername;

    public String sourcePath;

    public String destPath;

    public int windowSize;

    public long serverErrorRate;

    // -------- Variables
    // current default timeout in nanoseconds
    private long timeoutValue = 100000000L;
    private long jitterValue = timeoutValue / 4;

    // ... ToDo
    private SendBuffer sendBuffer;
    private boolean fileFinished = false;
    private int bufferIndex = 0;
    private InetAddress serverAddress;
    private DatagramSocket socket;
    private long sumOfRtts=0;
    private long noOfRtts=0;
    private long estimatedRTT = -1;
    private long deviation = 0;

    // Constructor
    public FileCopyClient(String serverArg, String sourcePathArg,
                          String destPathArg, String windowSizeArg, String errorRateArg) throws FileNotFoundException {
        servername = serverArg;
        sourcePath = sourcePathArg;
        destPath = destPathArg;
        windowSize = Integer.parseInt(windowSizeArg);
        serverErrorRate = Long.parseLong(errorRateArg);
        File file = new File(sourcePath);
        FileReader fileReader = new FileReader(file, UDP_PACKET_SIZE - 8);
        sendBuffer = new SendBuffer(windowSize, fileReader, this);

    }

    public static void main(String argv[]) throws Exception {
        FileCopyClient myClient = new FileCopyClient(argv[0], argv[1], argv[2],
                argv[3], argv[4]);
        myClient.runFileCopyClient();
    }

    public void runFileCopyClient() throws IOException {

        serverAddress = InetAddress.getByName(servername);
        socket = new DatagramSocket(CLIENT_PORT);

        ReceiverTask receiverTask = new ReceiverTask(socket, sendBuffer, serverAddress, SERVER_PORT);
        Thread receiverTaskThread = new Thread(receiverTask);
        receiverTaskThread.setName("receiverTaskThread");
        receiverTaskThread.start();

        FCpacket first = makeControlPacket();
        sendBuffer.initBuffer(first);

        boolean finished = false;
        while (sendBuffer.hasMore()) {
            FCpacket packet = sendBuffer.getNextToSend();
            if (packet != null) {
                System.out.println("Send buffered packet " + packet.getSeqNum());
                startTimer(packet,false);
                sendPackage(packet);
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    //nothing
                }
            }
        }
        receiverTaskThread.interrupt();
    }

    private void sendPackage(FCpacket fcPackage) throws IOException {
        DatagramPacket firstPackage = new DatagramPacket(fcPackage.getSeqNumBytesAndData(), fcPackage.getLen() + 8, serverAddress, SERVER_PORT);
        fcPackage.setTimestamp(System.nanoTime());
        socket.send(firstPackage);
        //System.out.println("Sent package with sequenz: " + fcPackage.getSeqNum());
    }

    /**
     * Timer Operations
     */
    public void startTimer(FCpacket packet, boolean resend) {
    /* Create, save and start timer for the given FCpacket */
        long timeout = resend ? timeoutValue * 2 : timeoutValue;
        FC_Timer timer = new FC_Timer(timeout, this, packet.getSeqNum());
        packet.setTimer(timer);
        timer.start();
    }

    public void cancelTimer(FCpacket packet) {
    /* Cancel timer for the given FCpacket */
        testOut("Cancel Timer for packet" + packet.getSeqNum());

        if (packet.getTimer() != null) {
            packet.getTimer().interrupt();
        }
    }

    /**
     * Implementation specific task performed at timeout
     */
    public void timeoutTask(long seqNum) {
        FCpacket fCpacket = sendBuffer.findPackageBySeqNum(seqNum);
        if (fCpacket == null) {
            return;
        }
        cancelTimer(fCpacket);
        startTimer(fCpacket, true);
        try {
            System.out.println("Send timeout packet " + fCpacket.getSeqNum());
            sendPackage(fCpacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Computes the current timeout value (in nanoseconds)
     */
    public void computeTimeoutValue(long sampleRTT) {

        sumOfRtts+=sampleRTT;
        noOfRtts++;

        if (estimatedRTT < 0) {
            estimatedRTT = sampleRTT;
        }

        double x = 0.1;

        estimatedRTT = (long) ((1 - x) * estimatedRTT + x * sampleRTT);

        //Deviation verstanden als sicherer Abstand
        deviation = (long) ((1 - x) * deviation + x * Math.abs(sampleRTT - estimatedRTT));
        timeoutValue = estimatedRTT + 4 * deviation;
        testOut("New timeout value: " + timeoutValue);
    }

    /**
     * Return value: FCPacket with (0 destPath;windowSize;errorRate)
     */
    public FCpacket makeControlPacket() {
   /* Create first packet with seq num 0. Return value: FCPacket with
     (0 destPath ; windowSize ; errorRate) */
        String sendString = destPath + ";" + windowSize + ";" + serverErrorRate;
        byte[] sendData = null;
        try {
            sendData = sendString.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new FCpacket(0, sendData, sendData.length);
    }

    public void testOut(String out) {
        if (TEST_OUTPUT_MODE) {
            System.err.printf("%,d %s: %s\n", System.nanoTime(), Thread
                    .currentThread().getName(), out);
        }
    }

}
