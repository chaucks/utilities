package com.xcoder.utilities;

import com.alibaba.fastjson.JSON;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author chuck lee.
 */
public class GzipUtensil {

    /**
     * 默认编码
     */
    public static final String DEFAULT_CHAR_SET = "ISO-8859-1";

    /**
     * UTF-8编码
     */
    public static final String UTF8_CHAR_SET = "UTF-8";

    /**
     * gzip 压缩
     *
     * @param object
     * @param charset
     * @return
     * @throws IOException
     */
    public static final byte[] compress(final Object object, final String charset) throws IOException {
        if (null == object) {
            return null;
        }
        ByteArrayOutputStream bos = null;
        GZIPOutputStream gos = null;
        try {
            bos = new ByteArrayOutputStream();
            gos = new GZIPOutputStream(bos);

            final String string = objectToString(object);
            final byte[] bytes = stringToByteArray(string, charset);
            gos.write(bytes);
        } finally {
            final boolean gosIsNotNull = null != gos;
            final boolean bosIsNotNull = null != bos;
            if (gosIsNotNull) {
                gos.flush();
            }
            if (bosIsNotNull) {
                bos.flush();
            }
            if (gosIsNotNull) {
                gos.close();
            }
            if (bosIsNotNull) {
                bos.close();
            }
        }
        return bos.toByteArray();
    }

    /**
     * gzip 解压
     *
     * @param bytes
     * @return
     * @throws IOException
     */
    public static final byte[] uncompress(final byte[] bytes) throws IOException {
        if (MixedUtensil.arrayEmpty(bytes)) {
            return null;
        }
        ByteArrayInputStream bis = null;
        GZIPInputStream gis = null;
        ByteArrayOutputStream bos = null;
        try {
            final int size = 1024 * 5;
            bis = new ByteArrayInputStream(bytes);
            gis = new GZIPInputStream(bis, size);
            bos = new ByteArrayOutputStream();
            final byte[] buffer = new byte[size];
            for (int r = gis.read(buffer); -1 != r; r = gis.read(buffer)) {
                bos.write(buffer, 0, r);
            }
        } finally {
            if (null != bos) {
                bos.flush();
                bos.close();
            }
            if (null != gis) {
                gis.close();
            }
            if (null != bis) {
                bis.close();
            }
        }
        return bos.toByteArray();
    }

    /**
     * object to string
     *
     * @param object
     * @return
     */
    public static final String objectToString(final Object object) {
        if (object instanceof String) {
            return (String) object;
        }
        return JSON.toJSONString(object);
    }

    /**
     * string to byte array
     *
     * @param string
     * @param charset
     * @return
     */
    public static final byte[] stringToByteArray(final String string, final String charset) throws UnsupportedEncodingException {
        if (null == charset) {
            return string.getBytes();
        }
        return string.getBytes(charset);
    }

    /**
     * byte array to string
     *
     * @param bytes
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     */
    public static final String byteArrayToString(final byte[] bytes, final String charset) throws UnsupportedEncodingException {
        if (null == charset) {
            return new String(bytes);
        }
        return new String(bytes, charset);
    }
}
