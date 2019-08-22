package com.xcoder.utilities.web;

import com.xcoder.utilities.IBrowser;
import com.xcoder.utilities.IUniversal;

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
public class WebUtensil implements IUniversal, IBrowser {

    /**
     * Poi file output write
     *
     * @param fileName fileName
     * @param function function
     * @param request  request
     * @param response response
     */
    public static void outWrite(String fileName, Function<OutputStream, Void> function, HttpServletRequest request, HttpServletResponse response) {
        String encodingFileName = getAndEncodingFileName(fileName, request.getHeader(USER_AGENT));
        String attachmentHeaderValue = ATTACHMENT_HEADER_VALUE_PREFIX.concat(encodingFileName);
        response.addHeader(ATTACHMENT_HEADER_NAME, attachmentHeaderValue);
        response.setCharacterEncoding(UTF_8_CHAR_SET);
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
                return new String(fileName.getBytes(UTF_8_CHAR_SET), ISO_8859_1_CHAR_SET);
            }
            // IE 360 QQ兼容
            return URLEncoder.encode(fileName, UTF_8_CHAR_SET);
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
        if (userAgent.startsWith(MOZILLA50)) {
            if (userAgent.endsWith(LIKEGECKO)) {
                return true;
            }
        }
        return userAgent.contains(MSIE);
    }
}
