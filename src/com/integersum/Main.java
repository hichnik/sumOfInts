package com.integersum;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

public class Main {

    private static long longSum = 0;

    public static String getFilePath(String fileName) {
        return Paths.get(fileName)
                .toAbsolutePath()
                .normalize()
                .toString();
    }

    public static void printUsage() {
        System.out.println( "Usage: ./sum filename" + "\n" +
                "where filenname is file to parse");
    }

    public static void calculateChunkSum(FileChannel inChannel, int bufferSize, long bufferOffset) {

        int currentInt;
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        try {
            inChannel.read(buffer, bufferOffset);

            for(int i = 0; i < bufferSize; i += 4) {
                currentInt = buffer.getInt(i);
                longSum += (long) currentInt;
                //System.out.println(longSum);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

    }

    public static void main(String[] args) throws IOException {

        long fileSize, offset;
        int chunks, lastChunkLen;

        // length of file buffer to read at once
        // should not be small - for example 640k
        // but here it`s ok

        int bufferLen = 8;

        // Print usage if caled without args/more than 1 arg
        if (args.length != 1) {
            printUsage();
            System.exit(1);
        }

        try {

            String currentFile = getFilePath(args[0]);

            RandomAccessFile integersFile = new RandomAccessFile(currentFile,"r");
            FileChannel inChannel = integersFile.getChannel();

            fileSize = inChannel.size();

            chunks = (int) fileSize / bufferLen;
            lastChunkLen = (int) fileSize % bufferLen;

            for (int i = 0; i < chunks; i++) {
                offset = i * bufferLen;
                calculateChunkSum(inChannel, bufferLen, offset);
            }
            if (lastChunkLen > 0) {
                calculateChunkSum(inChannel, lastChunkLen, chunks*bufferLen);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        // print result to console
        System.out.print(longSum);

    }
}
