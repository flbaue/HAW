package flbaue.rnp;

import java.io.*;

/**
 * Created by florian on 06.12.14.
 */
public class FileReader {

    private final BufferedInputStream in;
    private final int packetSize;
    private int sepNumber = 0;

    public FileReader(final File file, final int packetSize) throws FileNotFoundException {
        in = new BufferedInputStream(new FileInputStream(file));
        this.packetSize = packetSize;
    }

    public FCpacket getNextPackage() throws IOException {
        byte[] bytes = new byte[packetSize];
        int end = 0;
        end = in.read(bytes);
        if (end == -1) {
            return null;
        }
        sepNumber += 1;
        return new FCpacket(sepNumber, bytes, bytes.length);
    }

    public boolean hasMore() {
        return false;
    }
}
