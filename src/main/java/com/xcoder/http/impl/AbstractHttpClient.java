package com.xcoder.http.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xcoder.IUniversal;
import com.xcoder.http.IFileBinaryBody;
import com.xcoder.http.IStreamBinaryBody;
import com.xcoder.utilities.MixedUtensil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Abstract Http Client
 *
 * @author chuck lee.
 */
public abstract class AbstractHttpClient implements IUniversal, AutoCloseable {
    /**
     * multipart/form-data content type
     */
    public static final ContentType MULTIPART_FORM_DATA_CONTENT_TYPE = ContentType.MULTIPART_FORM_DATA;

    /**
     * multipart/form-data utf-8 charset content type
     */
    public static final ContentType MULTIPART_FORM_DATA_UTF8_CONTENT_TYPE = MULTIPART_FORM_DATA_CONTENT_TYPE.withCharset(UTF_8_CHAR_SET);

    /**
     * multipart/form-data content type string
     */
    public static final String MULTIPART_FORM_DATA_CONTENT_TYPE_STRING = MULTIPART_FORM_DATA_CONTENT_TYPE.toString();

    /**
     * multipart/form-data utf-8 charset content type string
     */
    public static final String MULTIPART_FORM_DATA_UTF8_CONTENT_TYPE_STRING = MULTIPART_FORM_DATA_UTF8_CONTENT_TYPE.toString();

    /**
     * application/json content type
     */
    public static final ContentType APPLICATION_JSON_CONTENT_TYPE = ContentType.APPLICATION_JSON;

    /**
     * application/json utf-8 charset content type
     */
    public static final ContentType APPLICATION_JSON_UTF8_CONTENT_TYPE = APPLICATION_JSON_CONTENT_TYPE.withCharset(UTF_8_CHAR_SET);

    /**
     * application/json content type string
     */
    public static final String APPLICATION_JSON_CONTENT_TYPE_STRING = APPLICATION_JSON_CONTENT_TYPE.toString();

    /**
     * application/json utf-8 charset content type string
     */
    public static final String APPLICATION_JSON_UTF8_CONTENT_TYPE_STRING = APPLICATION_JSON_UTF8_CONTENT_TYPE.toString();

    /**
     * 文件流暂存，释放资源用
     */
    private static final ThreadLocal<Collection<AutoCloseable>> AUTO_CLOSEABLE_CACHE = new ThreadLocal<>();

    /**
     * Default buffer capacity
     */
    public static final int DEFAULT_STRING_BUFFER_CAPACITY = 1024 * 100;

    /**
     * Buffer capacity
     */
    private volatile int stringBufferCapacity = DEFAULT_STRING_BUFFER_CAPACITY;

    /**
     * Default rest http client
     */
    public static final AbstractHttpClient DEFAULT_POST_CLIENT_REST = new AbstractHttpClient() {
        @Override
        public HttpRequestBase getHttpRequestBase(final String url, final Object... objects) {
            return getHttpPostRest(url, objects);
        }
    };

    /**
     * Default http client
     */
    public static final AbstractHttpClient DEFAULT_POST_CLIENT = new AbstractHttpClient() {
        @Override
        public HttpRequestBase getHttpRequestBase(String url, Object... objects) {
            return getHttpPost(url, objects);
        }
    };

    /**
     * Get HttpRequestBase
     *
     * @param url     url
     * @param objects objects
     * @return
     */
    public abstract HttpRequestBase getHttpRequestBase(final String url, final Object... objects);

    /**
     * Get Object result
     *
     * @param url     url
     * @param clazz   clazz
     * @param objects objects
     * @param <T>     T
     * @return
     * @throws Exception
     */
    public <T> T getResult(final String url, final Class<T> clazz, final Object... objects) throws Exception {
        return JSON.parseObject(getResult2(url, objects), clazz);
    }

    /**
     * Get String result
     *
     * @param url     url
     * @param objects objects
     * @return
     * @throws Exception
     */
    public final String getResult(final String url, final Object... objects) throws Exception {
        try (final CloseableHttpClient chc = HttpClients.createDefault();
             final CloseableHttpResponse chr = getCloseableHttpResponse(chc, url, objects);
             final InputStream is = getInputStream(chr);
             final InputStreamReader isr = getInputStreamReaderUTF8(is);
             final BufferedReader br = new BufferedReader(isr);
             final AutoCloseable ac = this) {
            final StringBuilder sb = new StringBuilder(this.stringBufferCapacity);
            for (String line = br.readLine(); null != line; line = br.readLine()) {
                sb.append(line);
            }
            final String rst = sb.toString();
            return rst;
        }
    }

    /**
     * Get String result using org.apache.http.util.EntityUtils
     *
     * @param url     url
     * @param objects objects
     * @return
     * @throws Exception
     */
    public final String getResult2(final String url, final Object... objects) throws Exception {
        try (final CloseableHttpClient chc = HttpClients.createDefault();
             final CloseableHttpResponse chr = getCloseableHttpResponse(chc, url, objects);
             final AutoCloseable ac = this) {
            final HttpEntity he = chr.getEntity();
            final String rst = EntityUtils.toString(he, UTF_8_CHAR_SET);
            return rst;
        }
    }

    /**
     * 关闭文件流资源
     *
     * @throws Exception
     */
    @Override
    public final void close() throws Exception {
        final Collection<AutoCloseable> caches = AUTO_CLOSEABLE_CACHE.get();
        if (null == caches) {
            return;
        }
        final Iterator<AutoCloseable> iterator = caches.iterator();
        while (iterator.hasNext()) {
            iterator.next().close();
            iterator.remove();
        }
        AUTO_CLOSEABLE_CACHE.remove();
    }

    /**
     * Get closeable http response.
     *
     * @param client  client
     * @param url     url
     * @param objects objects
     * @return
     * @throws IOException
     */
    private final CloseableHttpResponse getCloseableHttpResponse(final CloseableHttpClient client, final String url, final Object... objects) throws IOException {
        final HttpRequestBase httpRequestBase = this.getHttpRequestBase(url, objects);
        final CloseableHttpResponse closeableHttpResponse = client.execute(httpRequestBase);
        return closeableHttpResponse;
    }

    /**
     * 获取流
     *
     * @param closeableHttpResponse closeableHttpResponse
     * @return
     * @throws IOException
     */
    private final InputStream getInputStream(final CloseableHttpResponse closeableHttpResponse) throws IOException {
        final HttpEntity httpEntity = closeableHttpResponse.getEntity();
        return httpEntity.getContent();
    }

    /**
     * Get UTF-8 InputStreamReader
     *
     * @param inputStream inputStream
     * @return
     */
    private static final InputStreamReader getInputStreamReaderUTF8(final InputStream inputStream) throws UnsupportedEncodingException {
        return new InputStreamReader(inputStream, UTF_8_CHAR_SET);
    }

    /**
     * 获取HttpPost
     *
     * @param url     url
     * @param objects objects
     * @return
     * @throws IOException
     */
    public static final HttpPost getHttpPostRest(final String url, final Object... objects) {
        final HttpEntity httpEntity = getStringEntity(objects);
        final HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(httpEntity);
        httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON_UTF8_CONTENT_TYPE_STRING);
        return httpPost;
    }

    /**
     * 获取HttpPost
     *
     * @param url     url
     * @param objects objects
     * @return
     * @throws IOException
     */
    public static final HttpPost getHttpPost(final String url, final Object... objects) {
        final HttpEntity httpEntity = getMultipartEntity(objects);
        final HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(httpEntity);
        httpPost.addHeader(HTTP.CONTENT_TYPE, MULTIPART_FORM_DATA_UTF8_CONTENT_TYPE_STRING);
        return httpPost;
    }

    /**
     * String HttpEntity
     *
     * @param objects objects
     * @return
     */
    public static final StringEntity getStringEntity(final Object... objects) {
        return new StringEntity(getJSONString(objects), APPLICATION_JSON_UTF8_CONTENT_TYPE);
    }

    /**
     * Multipart HttpEntity
     *
     * @param objects objects
     * @return
     */
    public static final HttpEntity getMultipartEntity(final Object... objects) {
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setContentType(MULTIPART_FORM_DATA_UTF8_CONTENT_TYPE);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        /**
         * 添加body
         */
        addBody(builder, objects);
        return builder.build();
    }

    /**
     * JSONString
     *
     * @param objects objects
     * @return
     */
    public static final String getJSONString(final Object... objects) {
        if (MixedUtensil.arrayEmpty(objects)) {
            return "";
        }
        final JSONObject jsonObject = new JSONObject(8);
        final int length = objects.length;
        for (int i = 0; i < length; i++) {
            Object object = objects[i];
            if (object instanceof IFileBinaryBody) {
                continue;
            }
            if (object instanceof IStreamBinaryBody) {
                continue;
            }
            jsonObject.put((String) object, objects[++i]);
        }
        final String jsonString = jsonObject.toJSONString();
        return jsonString;
    }

    /**
     * 添加body
     *
     * @param builder builder
     * @param objects objects
     */
    private static final void addBody(final MultipartEntityBuilder builder, final Object... objects) {
        if (MixedUtensil.arrayEmpty(objects)) {
            return;
        }
        final int length = objects.length;
        for (int i = 0; i < length; i++) {
            Object object = objects[i];
            if (object instanceof IFileBinaryBody) {
                addBinaryBody(builder, (IFileBinaryBody) object);
                continue;
            }
            if (object instanceof IStreamBinaryBody) {
                addBinaryBody(builder, (IStreamBinaryBody) object);
                continue;
            }
            addTextBody(builder, (String) object, objects[++i]);
        }
    }

    /**
     * 添加文件body
     *
     * @param builder builder
     * @param body    body
     */
    private static final void addBinaryBody(final MultipartEntityBuilder builder, final IFileBinaryBody body) {
        builder.addBinaryBody(body.getName(), body.getFile(), body.getContentType(), body.getFileName());
    }

    /**
     * 添加文件流body
     *
     * @param builder builder
     * @param body    body
     */
    private static final void addBinaryBody(final MultipartEntityBuilder builder, final IStreamBinaryBody body) {
        builder.addBinaryBody(body.getName(), body.getStream(), body.getContentType(), body.getFileName());
        cacheIStreamBinaryBody(body);
    }

    /**
     * 添加文本body
     *
     * @param builder builder
     * @param name    name
     * @param object  object
     */
    private static final void addTextBody(final MultipartEntityBuilder builder, final String name, final Object object) {
        if (object instanceof String) {
            builder.addTextBody(name, (String) object, MULTIPART_FORM_DATA_UTF8_CONTENT_TYPE);
            return;
        }
        builder.addTextBody(name, JSON.toJSONString(object), MULTIPART_FORM_DATA_UTF8_CONTENT_TYPE);
    }

    /**
     * 缓存文件流body
     *
     * @param body body
     */
    private static final void cacheIStreamBinaryBody(final IStreamBinaryBody body) {
        if (null == AUTO_CLOSEABLE_CACHE.get()) {
            AUTO_CLOSEABLE_CACHE.set(new ArrayList<>(4));
        }
        AUTO_CLOSEABLE_CACHE.get().add(body);
    }

    /**
     * Get StringBuffer capacity
     *
     * @return
     */
    public final int getStringBufferCapacity() {
        return this.stringBufferCapacity;
    }

    /**
     * Get StringBuffer capacity
     *
     * @param stringBufferCapacity stringBufferCapacity
     */
    public final void setStringBufferCapacity(final int stringBufferCapacity) {
        this.stringBufferCapacity = stringBufferCapacity;
    }
}
