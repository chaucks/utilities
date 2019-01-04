package com.xcoder.http.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xcoder.IUniversal;
import com.xcoder.http.IFileBinaryBody;
import com.xcoder.http.IStreamBinaryBody;
import com.xcoder.utilities.MixedUtensil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
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
public abstract class AbstractHttpClient implements AutoCloseable, IUniversal {
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
     * Http server address (ip:port)
     */
    private final String serverAddress;

    /**
     * Constructor with server address
     *
     * @param serverAddress ip:port
     */
    public AbstractHttpClient(final String serverAddress) {
        MixedUtensil.stringEmptyRuntimeException(serverAddress, "Http server address can not be null. Please check...");
        this.serverAddress = serverAddress;
    }

    /**
     * Get http entry.
     *
     * @param objects
     * @return
     */
    public abstract HttpEntity getHttpEntity(final Object... objects);

    /**
     * Get Object result.
     *
     * @param url
     * @param clazz
     * @param objects
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T getResult(final String url, final Class<T> clazz, final Object... objects) throws Exception {
        return JSONObject.parseObject(getResult(url, objects), clazz);
    }

    /**
     * Get String result.
     *
     * @param url
     * @param objects
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
     * @param client
     * @param url
     * @param objects
     * @return
     * @throws IOException
     */
    private final InputStream getInputStream(final CloseableHttpClient client, final String url, final Object... objects) throws IOException {
        final HttpEntity httpEntity = client.execute(getHttpPost(url, objects)).getEntity();
        if (null != httpEntity) {
            return httpEntity.getContent();
        }
        return null;
    }

    /**
     * Get UTF-8 InputStreamReader
     *
     * @param inputStream
     * @return
     */
    private static final InputStreamReader getInputStreamReaderUTF8(final InputStream inputStream) throws UnsupportedEncodingException {
        return new InputStreamReader(inputStream, UTF_8_CHAR_SET);
    }

    /**
     * 获取HttpPost
     *
     * @param url
     * @param objects
     * @return
     * @throws IOException
     */
    private final HttpPost getHttpPost(final String url, final Object... objects) {
        final HttpPost httpPost = new HttpPost(this.serverAddress + url);
        final HttpEntity httpEntity = getHttpEntity(objects);
        httpPost.setEntity(httpEntity);
        return httpPost;
    }

    /**
     * String HttpEntity
     *
     * @param objects
     * @return
     */
    public static final StringEntity getStringEntity(final Object... objects) {
        return new StringEntity(getJSONString(objects), DEFAULT_CONTENT_TYPE_UTF8);
    }

    /**
     * Multipart HttpEntity
     *
     * @param objects
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
     * 添加body
     *
     * @param builder
     * @param objects
     */
    private static final void addBody(final MultipartEntityBuilder builder, final Object... objects) {
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
     * @param builder
     * @param body
     */
    private static final void addBinaryBody(final MultipartEntityBuilder builder, final IFileBinaryBody body) {
        builder.addBinaryBody(body.getName(), body.getFile(), body.getContentType(), body.getFileName());
    }

    /**
     * 添加文件流body
     *
     * @param builder
     * @param body
     */
    private static final void addBinaryBody(final MultipartEntityBuilder builder, final IStreamBinaryBody body) {
        builder.addBinaryBody(body.getName(), body.getStream(), body.getContentType(), body.getFileName());
        cacheIStreamBinaryBody(body);
    }

    /**
     * 添加文本body
     *
     * @param builder
     * @param name
     * @param object
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
     * @param body
     */
    private static final void cacheIStreamBinaryBody(final IStreamBinaryBody body) {
        if (null == AUTO_CLOSEABLE_CACHE.get()) {
            AUTO_CLOSEABLE_CACHE.set(new ArrayList<>(4));
        }
        AUTO_CLOSEABLE_CACHE.get().add(body);
    }

    /**
     * JSONString
     *
     * @param objects
     * @return
     */
    public static final String getJSONString(final Object... objects) {
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
}
