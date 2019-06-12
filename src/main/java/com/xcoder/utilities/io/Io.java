package com.xcoder.utilities.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * IO
 *
 * @author Chuck Lee
 * @date 2019-06-11
 */
public class Io {

    private static final long DEFAULT_EXPIRE = 10000L;

    private static final long DEFAULT_TIMEOUT = 50L;

    private static final int DEFAULT_LENGTH = 102400;

    /**
     * Write interface
     *
     * @author Chuck Lee
     * @date 2019-06-22
     */
    @FunctionalInterface
    public interface Iw {
        /**
         * Io write
         *
         * @param buffer buffer
         * @throws IOException IOException
         */
        void write(byte[] buffer) throws IOException;
    }

    /**
     * Read input stream and write output stream
     *
     * @param is InputStream
     * @param os OutputStream
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static void i2o(final InputStream is, final OutputStream os) throws IOException, InterruptedException {
        i2o(is, os, DEFAULT_EXPIRE, DEFAULT_TIMEOUT, DEFAULT_LENGTH);
    }

    /**
     * Read input stream and write output stream
     *
     * @param is      InputStream
     * @param os      OutputStream
     * @param expire  expire
     * @param timeout timeout
     * @param length  length
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static void i2o(final InputStream is, final OutputStream os, final long expire
            , final long timeout, final int length) throws IOException, InterruptedException {
        read(is, expire, timeout, length, buffer -> os.write(buffer));
        os.flush();
    }

    /**
     * Read input stream
     *
     * @param is      is
     * @param expire  expire
     * @param timeout timeout
     * @param length  length
     * @param iw      callback
     * @return available bytes
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static int read(final InputStream is, final long expire, final long timeout
            , final int length, final Iw iw) throws IOException, InterruptedException {
        final int available = available(is, expire, timeout);
        for (int i = 0, j = available / length; i < j; i++) {
            byte[] buffer = new byte[length];
            is.read(buffer);
            iw.write(buffer);
        }

        int remain = available % length;
        if (0 < remain) {
            byte[] buffer = new byte[remain];
            iw.write(buffer);
        }
        return available;
    }

    /**
     * Get InputStream available.
     *
     * @param is      InputStream
     * @param expire  expire
     * @param timeout timeout
     * @return available
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static int available(final InputStream is, final long expire, final long timeout) throws IOException, InterruptedException {
        int available = is.available();
        for (long t0 = System.currentTimeMillis(); 1 > available; available = is.available()) {
            if (expire < System.currentTimeMillis() - t0) {
                throw new RuntimeException("Waiting available time out......");
            }
            Thread.currentThread().wait(timeout);
        }
        return available;
    }

    public static void closeSockets(Socket... sockets) {
        for (Socket socket : sockets) {
            if (null == socket) {
                continue;
            }
            try {
                socket.close();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
