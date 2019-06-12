package com.xcoder.utilities.io;

import java.io.*;

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
     * Read byte array buffer
     *
     * @author Chuck Lee
     * @date 2019-06-22
     */
    @FunctionalInterface
    public interface Ir {
        /**
         * Io read
         *
         * @param buffer buffer
         * @throws IOException IOException
         */
        void read(byte[] buffer) throws IOException;
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
        final int available = available(is, expire, timeout);
        read(is, available, length, buffer -> os.write(buffer));
        os.flush();
    }

    /**
     * Read input stream get byte array
     *
     * @param is InputStream
     * @return byte array
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static byte[] read(final InputStream is) throws IOException, InterruptedException {
        int available = available(is, DEFAULT_EXPIRE, DEFAULT_TIMEOUT);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            read(is, available, available, buffer -> bos.write(buffer));
            return bos.toByteArray();
        } finally {
            closeableClose(bos);
        }
    }

    /**
     * Read input stream
     *
     * @param is     is
     * @param length length
     * @param ir     ir
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static void read(final InputStream is, final int available, final int length, final Ir ir) throws IOException {
        for (int i = 0, j = available / length; i < j; i++) {
            byte[] buffer = new byte[length];
            is.read(buffer);
            ir.read(buffer);
        }

        int remain = available % length;
        if (0 < remain) {
            byte[] buffer = new byte[remain];
            ir.read(buffer);
        }
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
            Thread.sleep(timeout);
        }
        return available;
    }

    /**
     * Closeable array close
     *
     * @param closeables closeables
     */
    public static void closeableClose(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (null == closeable) {
                continue;
            }
            try {
                closeable.close();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
