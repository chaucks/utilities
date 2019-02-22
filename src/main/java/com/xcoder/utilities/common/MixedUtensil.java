package com.xcoder.utilities.common;

import com.xcoder.utilities.IUniversal;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

/**
 * A common util file which contains most used methods
 *
 * @author chuck lee
 */
public class MixedUtensil implements IUniversal {
    /**
     * win10 ie ua 标识
     */
    public static final String WINDOWS_10_IE_USER_AGENT_LOWER_CASE = "mozilla/5.0 (windows nt 10.0; wow64; trident/7.0; rv:11.0) like gecko";

    /**
     * 字符串处理，默认StringBuffer capacity
     */
    public static final int DEFAULT_STRING_BUFFER_CAPACITY = 100;

    /**
     * jdk8 推荐日期格式化工具类 immutable 线程安全
     * LocalDate 2010-12-03
     * LocalTIME 11:05:03
     * LocalDateTime 2010-12-03T11:05:03
     * OffsetTime 11:05:03+01:00
     * OffsetDateTime 2010-12-03T11:05:03+01:00
     * ZonedDateTime 2010-12-03T11:05:03+01:00 Europe/Paris 时区相关
     * Year 2010
     * YearMonth 2010-12
     * MonthDay -12-01
     * Instant 2576458258.266 seconds after 1970-01-01
     */
    public static final DateTimeFormatter DEFAULT_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Integer 转 String
     *
     * @param obj obj
     * @return
     */
    public static final String objectToString(final Object obj) {
        return null == obj ? null : String.valueOf(obj);
    }

    /**
     * 字符串拼接
     *
     * @param objects objects
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
     * @param objects objects
     * @return
     */
    public static final boolean arrayNotEmpty(final Object... objects) {
        return !arrayEmpty(objects);
    }

    /**
     * 数组是否null或者空
     *
     * @param objects objects
     * @return
     */
    public static final boolean arrayEmpty(final Object... objects) {
        return null == objects || 0 >= objects.length;
    }

    /**
     * 集合不为null或非空判断
     *
     * @param collection collection
     * @return
     */
    public static final boolean collectionEmpty(final Collection collection) {
        return null == collection || 0 >= collection.size();
    }

    /**
     * 集合不为null或非空判断
     *
     * @param collection collection
     * @return
     */
    public static final boolean collectionNotEmpty(final Collection collection) {
        return !collectionEmpty(collection);
    }

    /**
     * 数组复制
     *
     * @param source source
     * @param target target
     * @param <T>    object
     * @return
     */
    public static final <T> T[] arrayCopy(final T[] source, final T... target) {
        int sourceLength = source.length;
        int targetLength = target.length;
        int targetLengthAddSourceLength = target.length + sourceLength;
        final T[] result = (T[]) new Object[targetLengthAddSourceLength];
        System.arraycopy(source, 0, result, targetLength, sourceLength);
        for (int i = 0; i < targetLength; i++) {
            result[i] = target[i];
        }
        return result;
    }

    /**
     * 通过user-agent判断是否ie浏览器
     *
     * @param userAgent userAgent
     * @return
     */
    public static final boolean isNotIE(final String userAgent) {
        return !isIE(userAgent);
    }

    /**
     * 通过user-agent判断是否ie浏览器
     *
     * @param userAgent userAgent
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
     * @param fileName fileName
     * @param request  request
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
     * @param fileName  fileName
     * @param userAgent userAgent
     * @return
     * @throws UnsupportedEncodingException
     */
    public static final String getAndEncodingFileName(final String fileName
            , final String userAgent) throws UnsupportedEncodingException {
        if (isNotIE(userAgent)) {
            return new String(fileName.getBytes(UTF_8_CHAR_SET), ISO_8859_1_CHAR_SET);
        }
        // IE 360 QQ兼容
        return URLEncoder.encode(fileName, UTF_8_CHAR_SET);
    }

    /**
     * 判断对象是否不为null
     *
     * @param object object
     * @return
     */
    public static final boolean objectNotNull(final Object object) {
        return !objectNull(object);
    }

    /**
     * 判断对象是否不为null
     *
     * @param object object
     * @return
     */
    public static final boolean objectNull(final Object object) {
        return null == object;
    }

    /**
     * 数组存在非null对象判断
     *
     * @param objects objects
     * @return
     */
    public static final boolean objectsObjectExists(final Object... objects) {
        if (objectNull(objects)) {
            return false;
        }
        for (Object object : objects) {
            if (objectNotNull(object)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 数组存在null对象判断
     *
     * @param objects objects
     * @return
     */
    public static final boolean objectsNullExists(final Object... objects) {
        if (objectNull(objects)) {
            return true;
        }
        for (Object object : objects) {
            if (objectNull(object)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 对象null异常检测
     *
     * @param object object
     */
    public static final void objectNullPointerException(final Object object) {
        objectNullPointerException(object, "");
    }

    /**
     * 对象null异常检测
     *
     * @param object  object
     * @param strings strings
     */
    public static final void objectNullPointerException(final Object object, final String... strings) {
        if (objectNull(object)) {
            throw new NullPointerException(appendString(strings));
        }
    }

    /**
     * 数组对象null异常检测
     *
     * @param objects objects
     */
    public static final void objectsNullPointerException(final Object... objects) {
        objectsNullPointerException(new String[]{""}, objects);
    }

    /**
     * 数组对象null异常检测
     *
     * @param strings strings
     * @param objects objects
     */
    public static final void objectsNullPointerException(final String[] strings, final Object... objects) {
        if (objectsNullExists(objects)) {
            throw new NullPointerException(appendString(strings));
        }
    }

    /**
     * 字符串empty异常检测
     *
     * @param string  string
     * @param strings strings
     */
    public static final void stringEmptyRuntimeException(final String string, final String... strings) {
        if (StringUtils.isEmpty(string)) {
            throw new RuntimeException(appendString(strings));
        }
    }

    /**
     * 获取默认格式的当前时间字符串
     *
     * @return
     */
    public static final String getFormatNow() {
        return DEFAULT_DTF.format(LocalDateTime.now());
    }

    /**
     * Read InputStream to String
     *
     * @param inputStream inputStream
     * @param charsetName charsetName
     * @param capacity    capacity
     * @return String
     * @throws IOException IOException
     */
    public static String readInputStream(final InputStream inputStream
            , final String charsetName, final int capacity) throws IOException {
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charsetName))) {
            final StringBuilder sb = new StringBuilder(capacity);
            for (String line = br.readLine(); null != line; line = br.readLine()) {
                sb.append(line);
            }
            final String rst = sb.toString();
            return rst;
        }
    }

}
