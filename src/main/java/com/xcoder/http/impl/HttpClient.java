package com.xcoder.http.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xcoder.http.IFileBinaryBody;
import com.xcoder.http.IStreamBinaryBody;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Http Client
 *
 * @author chuck lee.
 */
public class HttpClient implements AutoCloseable {
    /**
     * text body content type
     */
    public static final ContentType MULTIPART_FORM_DATA = ContentType.MULTIPART_FORM_DATA.withCharset("UTF-8");

    /**
     * 文件流暂存，释放资源用
     */
    private ThreadLocal<Collection<AutoCloseable>> autoCloseableCache = new ThreadLocal<>();

    /**
     * http char set
     */
    public static final String HTTP_CHAR_SET = "UTF-8";

    /**
     * 南农结果集封装
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
     * String结果
     *
     * @param url
     * @param objects
     * @return
     * @throws Exception
     */
    public String getResult(final String url, final Object... objects) throws Exception {
        try (InputStream is = getInputStream(url, objects);
             InputStreamReader isr = new InputStreamReader(is, HTTP_CHAR_SET);
             BufferedReader br = new BufferedReader(isr);
             AutoCloseable ac = this) {
            StringBuilder sb = new StringBuilder(200);
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
        final Collection<AutoCloseable> caches = this.autoCloseableCache.get();
        if (null == caches) {
            return;
        }
        final Iterator<AutoCloseable> iterator = caches.iterator();
        while (iterator.hasNext()) {
            iterator.next().close();
            iterator.remove();
        }
        this.autoCloseableCache.remove();
    }

    /**
     * 获取流
     *
     * @param url
     * @param objects
     * @return
     * @throws IOException
     */
    private final InputStream getInputStream(final String url, final Object... objects) throws IOException {
        HttpEntity httpEntity = HttpClients.createDefault().execute(getHttpPost(url, objects)).getEntity();
        if (null != httpEntity) {
            return httpEntity.getContent();
        }
        return null;
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
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        /**
         * 添加body
         */
        addBody(builder, objects);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(builder.build());
        return httpPost;
    }

    /**
     * 添加body
     *
     * @param builder
     * @param objects
     */
    private final void addBody(final MultipartEntityBuilder builder, final Object... objects) {
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
    private final void addBinaryBody(final MultipartEntityBuilder builder, final IFileBinaryBody body) {
        builder.addBinaryBody(body.getName(), body.getFile(), body.getContentType(), body.getFileName());
    }

    /**
     * 添加文件流body
     *
     * @param builder
     * @param body
     */
    private final void addBinaryBody(final MultipartEntityBuilder builder, final IStreamBinaryBody body) {
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
    private final void addTextBody(final MultipartEntityBuilder builder, final String name, final Object object) {
        if (object instanceof String) {
            builder.addTextBody(name, (String) object, MULTIPART_FORM_DATA);
            return;
        }
        builder.addTextBody(name, JSON.toJSONString(object), MULTIPART_FORM_DATA);
    }

    /**
     * 缓存文件流body
     *
     * @param body
     */
    private final void cacheIStreamBinaryBody(final IStreamBinaryBody body) {
        if (null == this.autoCloseableCache.get()) {
            this.autoCloseableCache.set(new ArrayList<>(4));
        }
        this.autoCloseableCache.get().add(body);
    }
}
