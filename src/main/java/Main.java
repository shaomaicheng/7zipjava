import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;


public class Main {

    private final class MyCreateCallback implements IOutCreateCallback<IOutItem7z> {

        @Override
        public void setOperationResult(boolean operationResultOk) throws SevenZipException {

        }

        @Override
        public IOutItem7z getItemInformation(int index, OutItemFactory<IOutItem7z> outItemFactory) throws SevenZipException {
            IOutItem7z item = outItemFactory.createOutItem();
            if (items.get(index).getContent() == null) {
                item.setPropertyIsDir(true);
            } else {
                item.setDataSize((long) items.get(index).getContent().length);
            }

            item.setPropertyPath(items.get(index).getPath());
            return item;
        }

        @Override
        public ISequentialInStream getStream(int index) throws SevenZipException {
            if (items.get(index).getContent() == null) {
                return null;
            }
            return new ByteArrayStream(items.get(index).getContent(), true);
        }

        @Override
        public void setTotal(long total) throws SevenZipException {

        }

        @Override
        public void setCompleted(long complete) throws SevenZipException {

        }
    }

    private ArrayList<CompressArchiveStructure.Item> items = new ArrayList<>();

    public static void main(String[] args) {
        String filename = "test.7z";
        new Main().compress(filename);

    }


    private void compress(String filename) {

        try {
            items = CompressArchiveStructure.create();
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean success = false;
        RandomAccessFile raf = null;

        IOutCreateArchive7z outArchive = null;

        try {
            raf = new RandomAccessFile(filename, "rw");

            outArchive = SevenZip.openOutArchive7z();
            outArchive.setLevel(9);
            outArchive.setSolid(true);

            outArchive.createArchive(new RandomAccessFileOutStream(raf), items.size(), new MyCreateCallback());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {


            if (outArchive != null) {
                try {
                    outArchive.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    success = false;
                }
            }

            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    success = false;
                    e.printStackTrace();
                }
            }

        }

        if (success) {
            System.out.println("Compression operation succeeded");
        }
    }



}
