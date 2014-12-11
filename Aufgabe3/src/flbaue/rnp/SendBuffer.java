package flbaue.rnp;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by florian on 06.12.14.
 */
public class SendBuffer {

    private final LinkedList<FCpacket> buffer = new LinkedList<>();
    private final int window;
    private final FileReader fileReader;
    private boolean fileEnd = false;
    private Set<Long> removedSeq = new HashSet<>();
    private final FileCopyClient fcc;

    public SendBuffer(int window, FileReader fileReader, FileCopyClient fcc) {
        this.window = window;
        this.fileReader = fileReader;
        this.fcc = fcc;
    }

    public void initBuffer(FCpacket packet) {
        if (packet.getSeqNum() != 0) {
            throw new IllegalArgumentException("Initial packet must have sequence number 0");
        }

        buffer.addFirst(packet);
        fillBuffer();
    }

    synchronized public FCpacket getNextToSend() {
        int index = sendIndex();
        if (index == -1) {
            return null;
        }
        FCpacket nextPackage = buffer.get(index);
        nextPackage.send();
        return nextPackage;
    }

    synchronized private int sendIndex() {
        for (int i = 0; i < buffer.size(); i++) {
            if (buffer.get(i).isSent() == false) {
                System.out.println("sendIndex: Next to send is packet " + buffer.get(i).getSeqNum());
                return i;
            }
        }
        System.out.println("sendIndex: All packages in buffer are sent");
        return -1;
    }

    synchronized public void acknowledgePackage(FCpacket ackPacket) {
        System.out.println("CALL BUFFER acknowledgePackage " + ackPacket.getSeqNum());
        FCpacket packet = null;
        for (FCpacket p : buffer) {
            if (p.getSeqNum() == ackPacket.getSeqNum()) {
                packet = p;
                break;
            }
        }
        if (packet == null) {
            System.out.println("BUFFER " + "Acknowledged package " + ackPacket.getSeqNum() + " is not in the buffer");
            return;
            //throw new RuntimeException("Acknowledged package " + ackPacket.getSeqNum() + " is not in the buffer");
        }

        packet.setValidACK(true);
        packet.getTimer().interrupt();
        System.out.println("Acknowledged packet " + packet.getSeqNum());
        fcc.computeTimeoutValue(System.nanoTime() - packet.getTimestamp());
        //packet.setTimer(null);


        while (!buffer.isEmpty() && buffer.getFirst().isValidACK()) {
            System.out.println("BUFFER remove packet " + buffer.getFirst().getSeqNum());
            removedSeq.add(buffer.getFirst().getSeqNum());
            buffer.removeFirst();
        }
        fillBuffer();
    }

    synchronized public FCpacket findPackageBySeqNum(long seqNumber) {
        for (FCpacket packet : buffer) {
            if (seqNumber == packet.getSeqNum()) {
                return packet;
            }
        }
        if (removedSeq.contains(seqNumber)) {
            return null;
        }
        throw new IllegalArgumentException("Package with SeqNum " + seqNumber + " is not in the buffer");
    }

   synchronized private void fillBuffer() {
        try {
            while (buffer.size() < window && !fileEnd) {
                FCpacket packet = fileReader.getNextPackage();
                if (packet != null) {
                    buffer.addLast(packet);
                } else {
                    fileEnd = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read next file package");
        }
    }

    synchronized public boolean hasMore() {
        return buffer.size() > 0 && !fileEnd;
    }
}
