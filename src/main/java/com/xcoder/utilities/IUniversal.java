package com.xcoder.utilities;

/**
 * 通用接口
 *
 * @author chuck lee
 */
public interface IUniversal {
    /**
     * ISO-8859-1编码
     */
    String ISO_8859_1_CHAR_SET = "ISO-8859-1";

    /**
     * UTF-8编码
     */
    String UTF_8_CHAR_SET = "UTF-8";

    /**
     * Http put request
     */
    String HTTP_PUT = "PUT";

    /**
     * Http get request
     */
    String HTTP_GET = "GET";

    /**
     * Http post request
     */
    String HTTP_POST = "POST";

    /**
     * Http delete request
     */
    String HTTP_DELETE = "DELETE";

    /**
     * Http options request
     */
    String HTTP_OPTIONS = "OPTIONS";

    /**
     * Http user agent
     */
    String USER_AGENT = "user-agent";

    String ATTACHMENT_HEADER_NAME = "Content-Disposition";

    String ATTACHMENT_HEADER_VALUE_PREFIX = "attachment;filename=";
}
