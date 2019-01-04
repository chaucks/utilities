package com.xcoder.http.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xcoder.IUniversal;
import com.xcoder.http.IFileBinaryBody;
import com.xcoder.http.IStreamBinaryBody;
import com.xcoder.utilities.MixedUtensil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

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
     * text body content type
     */
    public static final ContentType MULTIPART_FORM_DATA = ContentType.MULTIPART_FORM_DATA;

    /**
     * text body content type utf-8 charset
     */
    public static final ContentType MULTIPART_FORM_DATA_UTF8 = MULTIPART_FORM_DATA.withCharset(UTF_8_CHAR_SET);

    /**
     * default content type
     */
    public static final ContentType DEFAULT_CONTENT_TYPE = ContentType.APPLICATION_JSON;

    /**
     * default content type utf-8 charset
     */
    public static final ContentType DEFAULT_CONTENT_TYPE_UTF8 = DEFAULT_CONTENT_TYPE.withCharset(UTF_8_CHAR_SET);

    /**
     * 文件流暂存，释放资源用
     */
    private static final ThreadLocal<Collection<AutoCloseable>> AUTO_CLOSEABLE_CACHE = new ThreadLocal<>();

    /**
     * Default rest http client.
     */
    public static final AbstractHttpClient DEFAULT_POST_CLIENT_REST = new AbstractHttpClient() {
        @Override
        public HttpRequestBase getHttpRequestBase(final String url, final Object... objects) {
            return getHttpPostRest(url, objects);
        }
    };

    /**
     * Default http client.
     */
    public static final AbstractHttpClient DEFAULT_POST_CLIENT = new AbstractHttpClient() {
        @Override
        public HttpRequestBase getHttpRequestBase(String url, Object... objects) {
            return getHttpPost(url, objects);
        }
    };

    /**
     * Get HttpRequestBase.
     *
     * @param url     url
     * @param objects objects
     * @return
     */
    public abstract HttpRequestBase getHttpRequestBase(final String url, final Object... objects);

    /**
     * Get Object result.
     *
     * @param url     url
     * @param clazz   clazz
     * @param objects objects
     * @param <T>     T
     * @return
     * @throws Exception
     */
    public <T> T getResult(final String url, final Class<T> clazz, final Object... objects) throws Exception {
        return JSONObject.parseObject(getResult(url, objects), clazz);
    }

    /**
     * Get String result.
     *
     * @param url     url
     * @param objects objects
     * @return
     * @throws Exception
     */
    public final String getResult(final String url, final Object... objects) throws Exception {
        try (final CloseableHttpClient client = HttpClients.createDefault();
             final InputStream is = getInputStream(client, url, objects);
             final InputStreamReader isr = getInputStreamReaderUTF8(is);
             final BufferedReader br = new BufferedReader(isr);
             final AutoCloseable ac = this) {
            final StringBuilder sb = new StringBuilder(200);
            for (String line = br.readLine(); null != line; line = br.readLine()) {
                sb.append(line);
            }
            String rst = sb.toString();
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
     * 获取流
     *
     * @param client  client
     * @param url     url
     * @param objects objects
     * @return
     * @throws IOException
     */
    private final InputStream getInputStream(final CloseableHttpClient client, final String url, final Object... objects) throws IOException {
        final HttpRequestBase httpRequestBase = this.getHttpRequestBase(url, objects);
        final HttpEntity httpEntity = client.execute(httpRequestBase).getEntity();
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
        return getHttpPost(url, httpEntity);
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
        return getHttpPost(url, httpEntity);
    }

    /**
     * String HttpEntity
     *
     * @param objects objects
     * @return
     */
    public static final StringEntity getStringEntity(final Object... objects) {
        return new StringEntity(getJSONString(objects), DEFAULT_CONTENT_TYPE_UTF8);
    }

    /**
     * Multipart HttpEntity
     *
     * @param objects objects
     * @return
     */
    public static final HttpEntity getMultipartEntity(final Object... objects) {
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setContentType(MULTIPART_FORM_DATA_UTF8);
        /**
         * 添加body
         */
        addBody(builder, objects);
        return builder.build();
    }

    /**
     * New HttpPost and set HttpEntity.
     *
     * @param url        url
     * @param httpEntity HttpEntity
     * @return
     */
    private static final HttpPost getHttpPost(final String url, final HttpEntity httpEntity) {
        final HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(httpEntity);
        return httpPost;
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
            builder.addTextBody(name, (String) object, MULTIPART_FORM_DATA_UTF8);
            return;
        }
        builder.addTextBody(name, JSON.toJSONString(object), MULTIPART_FORM_DATA_UTF8);
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

}
