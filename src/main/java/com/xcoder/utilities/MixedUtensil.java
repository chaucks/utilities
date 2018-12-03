package com.xcoder.utilities;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

/**
 * A common util file which comtains most used methods.
 *
 * @author chuck lee
 */
public class MixedUtensil {
    /**
     * win10 ie ua 标识
     */
    public static final String WINDOWS_10_IE_USER_AGENT_LOWER_CASE = "mozilla/5.0 (windows nt 10.0; wow64; trident/7.0; rv:11.0) like gecko";

    /**
     * 字符串处理，默认StringBuffer capacity
     */
    public static final int DEFAULT_STRING_BUFFER_CAPACITY = 100;

    /**
     * Integer 转 String
     *
     * @param obj
     * @return
     */
    public static final String objectToString(final Object obj) {
        return null == obj ? null : String.valueOf(obj);
    }

    /**
     * 字符串拼接
     *
     * @param objects
     * @return
     */
    public static final String appendString(Object... objects) {
        final StringBuffer buffer = new StringBuffer(DEFAULT_STRING_BUFFER_CAPACITY);
        for (Object object : objects) {
            buffer.append(object);
        }
        return buffer.toString();
    }

    /**
     * 数组是否不为null或者非空
     *
     * @param objects
     * @return
     */
    public static final boolean arrayNotEmpty(final Object... objects) {
        return !arrayEmpty(objects);
    }

    /**
     * 数组是否null或者空
     *
     * @param objects
     * @return
     */
    public static final boolean arrayEmpty(final Object... objects) {
        return null == objects || 0 >= objects.length;
    }

    /**
     * 集合不为null或非空判断
     *
     * @param collection
     * @return
     */
    public static final boolean collectionEmpty(final Collection collection) {
        return null == collection || 0 >= collection.size();
    }

    /**
     * 集合不为null或非空判断
     *
     * @param collection
     * @return
     */
    public static final boolean collectionNotEmpty(final Collection collection) {
        return !collectionEmpty(collection);
    }

    /**
     * 数组复制
     *
     * @param source
     * @param target
     * @return
     */
    public static final String[] arrayCopy(String[] source, String... target) {
        int sourceLength = source.length;
        int targetLength = target.length;
        int targetLengthAddSourceLength = target.length + sourceLength;
        String[] result = new String[targetLengthAddSourceLength];
        System.arraycopy(source, 0, result, targetLength, sourceLength);
        for (int i = 0; i < targetLength; i++) {
            result[i] = target[i];
        }
        return result;
    }

    /**
     * 通过user-agent判断是否ie浏览器
     *
     * @param userAgent
     * @return
     */
    public static final boolean isNotIE(final String userAgent) {
        return !isIE(userAgent);
    }

    /**
     * 通过user-agent判断是否ie浏览器
     *
     * @param userAgent
     * @return
     */
    public static final boolean isIE(final String userAgent) {
        if (userAgent.startsWith("mozilla/5.0")) {
            if (userAgent.endsWith("like gecko")) {
                return true;
            }
        }
        if (userAgent.contains("msie")) {
            return true;
        }
        return false;
    }

    /**
     * 文件下载，文件名编码
     *
     * @param fileName
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    public static final String getAndEncodingFileName(final String fileName
            , final HttpServletRequest request) throws UnsupportedEncodingException {
        final String userAgent = request.getHeader("user-agent").toLowerCase();
        return getAndEncodingFileName(fileName, userAgent);
    }

    /**
     * 文件下载，文件名编码
     *
     * @param fileName
     * @param userAgent
     * @return
     * @throws UnsupportedEncodingException
     */
    public static final String getAndEncodingFileName(final String fileName
            , final String userAgent) throws UnsupportedEncodingException {
        if (isNotIE(userAgent)) {
            return new String(fileName.getBytes("UTF-8"), "iso8859-1");
        }
        // IE 360 QQ兼容
        return URLEncoder.encode(fileName, "UTF-8");
    }

    /**
     * 判断对象是否不为null
     *
     * @param object
     * @return
     */
    public static final boolean objectNotNull(final Object object) {
        return !objectNull(object);
    }

    /**
     * 判断对象是否不为null
     *
     * @param object
     * @return
     */
    public static final boolean objectNull(final Object object) {
        return null == object;
    }

    /**
     * 对象null异常检测
     *
     * @param object
     */
    public static final void objectNullPointerException(final Object object) {
        objectNullPointerException(object, "");
    }

    /**
     * 对象null异常检测
     *
     * @param object
     * @param strings
     */
    public static final void objectNullPointerException(final Object object, final String... strings) {
        if (objectNull(object)) {
            throw new NullPointerException(appendString(strings));
        }
    }
}
