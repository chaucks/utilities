package com.xcoder.utilities.http.rs;

import java.io.InputStream;

/**
 * Stream BinaryBody Interface
 *
 * @author chuck lee.
 */
public interface IStreamBinaryBody extends IBinaryBody, AutoCloseable {
    /**
     * 文件流
     *
     * @return
     */
    InputStream getStream();
}
