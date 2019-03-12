package com.xcoder.utilities.http.rs;

import org.apache.http.entity.ContentType;

/**
 * BinaryBody Interface
 *
 * @author chuck lee.
 */
public interface IBinaryBody/* extends AutoCloseable*/ {

    /**
     * HTTP 参数名
     *
     * @return
     */
    String getName();

    /**
     * HTTP 文件名
     *
     * @return
     */
    String getFileName();

    /**
     * HTTP contentType
     *
     * @return
     */
    ContentType getContentType();
}
