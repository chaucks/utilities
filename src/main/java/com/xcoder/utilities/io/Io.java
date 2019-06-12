package com.xcoder.utilities.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
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
     * Port to port
     *
     * @param port0 port0
     * @param port1 port1
     * @throws IOException IOException
     */
    public static void p2p(int port0, int port1) throws IOException {
        ServerSocket serverSocket0 = new ServerSocket(port0);
        ServerSocket serverSocket1 = new ServerSocket(port1);
        Socket socket0 = null;
        Socket socket1 = null;
        for (; ; ) {
            try {
                socket0 = serverSocket0.accept();
                socket1 = serverSocket1.accept();
            } catch (Throwable t) {
                t.printStackTrace();
                closeSockets(socket0, socket1);
                continue;
            }
            s2s(socket0, socket1);
        }
    }

    /**
     * Port to host
     *
     * @param port0 port0
     * @param host1 host1
     * @param port1 port1
     * @throws IOException IOException
     */
    public static void p2h(int port0, String host1, int port1) throws IOException {
        ServerSocket serverSocket0 = new ServerSocket(port0);
        Socket socket0 = null;
        Socket socket1 = null;
        for (; ; ) {
            try {
                socket0 = serverSocket0.accept();
                socket1 = new Socket(host1, port1);
            } catch (Throwable t) {
                t.printStackTrace();
                closeSockets(socket0, socket1);
                continue;
            }
            s2s(socket0, socket1);
        }
    }

    /**
     * Host to host
     *
     * @param host0 host0
     * @param port0 port0
     * @param host1 host1
     * @param port1 port1
     */
    public static void h2h(String host0, int port0, String host1, int port1) {
        Socket socket0 = null;
        Socket socket1 = null;
        for (; ; ) {
            try {
                socket0 = new Socket(host0, port0);
                socket1 = new Socket(host1, port1);
            } catch (Throwable t) {
                t.printStackTrace();
                closeSockets(socket0, socket1);
                continue;
            }
            s2s(socket0, socket1);
        }
    }

    public static void s2s(final Socket socket0, final Socket socket1) {
        si2so(socket0, socket1);
        si2so(socket1, socket0);
        try {
            socket1.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        try {
            socket0.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void si2so(final Socket socket0, final Socket socket1) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = socket0.getInputStream();
            os = socket1.getOutputStream();
            i2o(is, os);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            try {
                if (null != os) {
                    os.close();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
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
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static void read(final InputStream is, final long expire, final long timeout
            , final int length, final Iw iw) throws IOException, InterruptedException {
        int available = available(is, expire, timeout);
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
