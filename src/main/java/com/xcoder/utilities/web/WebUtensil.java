package com.xcoder.utilities.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Function;

/**
 * Web utensil
 *
 * @author Chuck Lee
 */
public class WebUtensil {

    private static final String USER_AGENT = "user-agent";

    private static final String ISO8859_1 = "iso8859-1";

    private static final String UTF_8 = "utf-8";

    private static final String ATTACHMENT_HEADER_NAME = "Content-Disposition";

    private static final String ATTACHMENT_HEADER_VALUE_PREFIX = "attachment;filename=";

    /**
     * Poi file output write
     *
     * @param fileName fileName
     * @param function function
     * @param request  request
     * @param response response
     */
    public static void poiWrite(String fileName, Function<OutputStream, Void> function, HttpServletRequest request, HttpServletResponse response) {
        String encodingFileName = getAndEncodingFileName(fileName, request.getHeader(USER_AGENT));
        String attachmentHeaderValue = ATTACHMENT_HEADER_VALUE_PREFIX.concat(encodingFileName);
        response.addHeader(ATTACHMENT_HEADER_NAME, attachmentHeaderValue);
        response.setCharacterEncoding(UTF_8);
        try (OutputStream out = response.getOutputStream()) {
            function.apply(out);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * File name encoding
     *
     * @param fileName  file name
     * @param userAgent userAgent
     * @return Encoded file name
     */
    public static String getAndEncodingFileName(final String fileName, final String userAgent) {
        try {
            if (isNotIE(userAgent.toLowerCase())) {
                return new String(fileName.getBytes(UTF_8), ISO8859_1);
            }
            // IE 360 QQ兼容
            return URLEncoder.encode(fileName, UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Is not IE explorer
     *
     * @param userAgent ua
     * @return true not ie
     */
    public static boolean isNotIE(final String userAgent) {
        return !isIE(userAgent);
    }

    /**
     * Is IE explorer
     *
     * @param userAgent ua
     * @return true is ie
     */
    public static boolean isIE(final String userAgent) {
        if (userAgent.startsWith("mozilla/5.0")) {
            if (userAgent.endsWith("like gecko")) {
                return true;
            }
        }
        return userAgent.contains("msie");
    }
}
