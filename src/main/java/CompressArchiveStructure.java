import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class CompressArchiveStructure {

    public static ArrayList<Item> create() throws IOException {
        ArrayList<Item> items = new ArrayList<>();
        File dir = new File("temp");
        if (dir.exists()) {
            File scriptDir = new File(dir, "YZScriptCaches");
            addItem(scriptDir, items);

            File imageDir = new File(dir,"YZImageCache");
            addItem(imageDir, items);
        }
        return items;
    }


    private static void addItem(File dir, ArrayList<Item> items) throws IOException {
        if (dir.exists() && dir.isDirectory()) {
            File[] images = dir.listFiles();
            if (images != null) {
                for (File file : images) {
                    FileChannel channel = new RandomAccessFile(file.getAbsoluteFile(), "r").getChannel();
                    MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).load();
                    byte[] temp = new byte[(int) channel.size()];
                    if (byteBuffer.remaining() > 0) {
                        byteBuffer.get(temp, 0, byteBuffer.remaining());
                    }
                    Item item = new Item(file.getPath(), temp);
                    items.add(item);
                }
            }
        }
    }

    static class Item {
        private String path;
        private byte[] content;

        Item(String path, String content) {
            this(path, content.getBytes());
        }

        Item(String path, byte[] content) {
            this.path = path;
            this.content = content;
        }


        String getPath() {
            return path;
        }

        byte[] getContent() {
            return content;
        }
    }
}
