package com.xcoder.utilities.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IO
 *
 * @author Chuck Lee
 * @date 2019-06-11
 */
public class Io {

    private InputStream is;

    private OutputStream os;

    private long expire = 10000L;

    private long timeout = 50L;

    private int length = 102400;

    public Io(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
    }

    public Io(InputStream is, OutputStream os, long expire, long timeout, int length) {
        this(is, os);
        this.expire = expire;
        this.timeout = timeout;
        this.length = length;
    }

    /**
     * Read input stream and write output stream
     *
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public final void readWrite() throws IOException, InterruptedException {
        int available = available(this.is, this.expire, this.timeout, this);
        for (int i = 0, j = available / this.length; i < j; i++) {
            byte[] buffer = new byte[this.length];
            this.is.read(buffer);
            this.os.write(buffer);
        }

        int remain = available % this.length;
        if (0 < remain) {
            byte[] buffer = new byte[remain];
            this.os.write(buffer);
        }
        this.os.flush();
    }

    /**
     * Get InputStream available.
     *
     * @param is      is
     * @param expire  expire
     * @param timeout timeout
     * @param monitor monitor
     * @return available
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static int available(final InputStream is, final long expire, final long timeout, final Object monitor) throws IOException, InterruptedException {
        int available = is.available();
        for (long t0 = System.currentTimeMillis(); 1 > available; available = is.available()) {
            if (expire < System.currentTimeMillis() - t0) {
                throw new RuntimeException("Waiting available time out......");
            }
            monitor.wait(timeout);
        }
        return available;
    }
}
