package com.xcoder.utilities.http.rs;

import com.xcoder.utilities.IUniversal;
import org.apache.http.entity.ContentType;

/**
 * BinaryBody Interface
 *
 * @author chuck lee.
 */
public interface IBinaryBody extends IUniversal/*, AutoCloseable*/ {

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
