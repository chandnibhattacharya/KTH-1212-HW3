package common.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

/**
 * 
 * File handling over a TCP socket using NIO.
 */
public class FileTransferHandler {
  private static final int initBufferSize = 1024;

  /**
   * Handles receiving of a file over a TCP socket.
   *
   * @param channel The channel to receive the file from.
   * @param path Path where to save the file.
   * @param size Size of the file to receive.
   * @throws IOException If something goes wrong with the file transfer.
   */
  public static void receiveFile(SocketChannel channel, Path path, long size) throws IOException {
    try (FileChannel fileChannel = FileChannel.open(path,
        EnumSet.of(StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.WRITE))) {

      ByteBuffer buffer = ByteBuffer.allocate(initBufferSize);

      long bytesReceived = 0;

      while (bytesReceived < size) {
        long channelReadBytes = channel.read(buffer);
        if (channelReadBytes <= 0) break;
        bytesReceived += channelReadBytes;

        buffer.flip();
        fileChannel.write(buffer);
        buffer.clear();
      }
    }
  }

  /**
   * Handles sending of a file over a TCP socket.
   *
   * @param channel The channel to receive the file from.
   * @param path Path to the file to upload.
   * @throws IOException If something goes wrong with the file transfer.
   */
  public static void sendFile(SocketChannel channel, Path path) throws IOException {
    try (FileChannel fileChannel = FileChannel.open(path)) {
      ByteBuffer buffer = ByteBuffer.allocate(initBufferSize);

      while (fileChannel.read(buffer) > 0) {
        buffer.flip();
        channel.write(buffer);
        buffer.clear();
      }
    }
  }
}
