package com.xcoder.utilities.net.napt;

import com.xcoder.utilities.io.Io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Network Address Port Translation.
 *
 * @author Chuck Lee
 * @date 2019-06-12
 */
public class Napt {
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
                Io.closeSockets(socket0, socket1);
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
                Io.closeSockets(socket0, socket1);
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
                Io.closeSockets(socket0, socket1);
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
            Io.i2o(is, os);
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
}
