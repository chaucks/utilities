package com.xcoder.utilities.http;

import java.io.File;

/**
 * File BinaryBody Interface
 *
 * @author chuck lee.
 */
public interface IFileBinaryBody extends IBinaryBody {
    /**
     * HTTP File
     *
     * @return
     */
    File getFile();
}
