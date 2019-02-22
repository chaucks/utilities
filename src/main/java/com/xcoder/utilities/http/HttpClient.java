package com.xcoder.utilities.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xcoder.utilities.common.MixedUtensil;
import com.xcoder.utilities.http.rs.IFileBinaryBody;
import com.xcoder.utilities.http.rs.IStreamBinaryBody;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.nio.charset.Charset;

/**
 * Http client
 *
 * @author Chuck Lee
 */
public class HttpClient {

    /**
     * Rest http client
     */
    private final AbstractHttpClient restClient;

    /**
     * Multipart http client
     */
    private final AbstractHttpClient multiClient;

    /**
     * Constructor
     *
     * @param serverAddress serverAddress
     */
    public HttpClient(final String serverAddress) {
        this.restClient = new AbstractHttpClient(serverAddress) {
            /**
             * Charset
             */
            private final Charset charset = Charset.forName(UTF_8_CHAR_SET);

            /**
             * Http entry content type
             * application/json;charset=utf-8
             */
            private final ContentType contentType = ContentType.APPLICATION_JSON.withCharset(this.charset);

            /**
             * Http header content type
             */
            private final String contentTypeS = this.contentType.toString();

            @Override
            public final String getContentType() {
                return this.contentTypeS;
            }

            @Override
            public final HttpEntity getHttpEntity(final Object... objects) {
                final String jsonString = this.getJSONString(objects);
                return new StringEntity(jsonString, this.contentType);
            }

            @Override
            public final Charset getCharset() {
                return this.charset;
            }

            /**
             * JSONString
             *
             * @param objects objects
             * @return String
             */
            private String getJSONString(final Object... objects) {
                if (MixedUtensil.arrayEmpty(objects)) {
                    return "";
                }
                final JSONObject json = new JSONObject(8);
                final int length = objects.length;
                for (int i = 0; i < length; i++) {
                    Object object = objects[i];
                    if (object instanceof IFileBinaryBody) {
                        continue;
                    }
                    if (object instanceof IStreamBinaryBody) {
                        continue;
                    }
                    json.put((String) object, objects[++i]);
                }
                final String jsonString = json.toJSONString();
                return jsonString;
            }
        };

        this.multiClient = new AbstractHttpClient(serverAddress) {
            /**
             * Charset
             */
            private final Charset charset = Charset.forName(UTF_8_CHAR_SET);

            /**
             * Http entry content type
             * multipart/form-data;charset=utf-8
             */
            private final ContentType contentType = ContentType.MULTIPART_FORM_DATA.withCharset(this.charset);

            /**
             * Http header content type
             */
            private final String contentTypeS = this.contentType.toString();

            @Override
            public String getContentType() {
                return this.contentTypeS;
            }

            @Override
            public HttpEntity getHttpEntity(final Object... objects) {
                final MultipartEntityBuilder meb = MultipartEntityBuilder.create();
                meb.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                meb.setContentType(this.contentType);
                this.addBody(meb, objects);
                return meb.build();
            }

            @Override
            public Charset getCharset() {
                return this.charset;
            }

            /**
             * 添加body
             *
             * @param builder builder
             * @param objects objects
             */
            private void addBody(final MultipartEntityBuilder builder, final Object... objects) {
                if (MixedUtensil.arrayEmpty(objects)) {
                    return;
                }
                final int length = objects.length;
                for (int i = 0; i < length; i++) {
                    Object object = objects[i];
                    if (object instanceof IFileBinaryBody) {
                        this.addBinaryBody(builder, (IFileBinaryBody) object);
                        continue;
                    }
                    if (object instanceof IStreamBinaryBody) {
                        this.addBinaryBody(builder, (IStreamBinaryBody) object);
                        continue;
                    }
                    this.addTextBody(builder, (String) object, objects[++i]);
                }
            }

            /**
             * 添加文件body
             *
             * @param builder builder
             * @param body    body
             */
            private void addBinaryBody(final MultipartEntityBuilder builder, final IFileBinaryBody body) {
                builder.addBinaryBody(body.getName(), body.getFile(), body.getContentType(), body.getFileName());
            }

            /**
             * 添加文件流body
             *
             * @param builder builder
             * @param body    body
             */
            private void addBinaryBody(final MultipartEntityBuilder builder, final IStreamBinaryBody body) {
                builder.addBinaryBody(body.getName(), body.getStream(), body.getContentType(), body.getFileName());
                // ThreadLocal缓存IStreamBinaryBody
                cacheIStreamBinaryBody(body);
            }

            /**
             * 添加文本body
             *
             * @param builder builder
             * @param name    name
             * @param object  object
             */
            private void addTextBody(final MultipartEntityBuilder builder, final String name, final Object object) {
                if (object instanceof String) {
                    builder.addTextBody(name, (String) object, this.contentType);
                    return;
                }
                builder.addTextBody(name, JSON.toJSONString(object), this.contentType);
            }
        };

    }

    /**
     * Http post restful
     *
     * @param router  router
     * @param objects objects
     * @return String
     * @throws Exception Exception
     */
    public final String postRest(final String router, final Object... objects) throws Exception {
        return this.restClient.post(router, objects);
    }

    /**
     * Http post restful
     *
     * @param router  router
     * @param clazz   clazz
     * @param objects objects
     * @param <T>     Any
     * @return T Any
     * @throws Exception Exception
     */
    public final <T> T postRest(final String router, final Class<T> clazz, final Object... objects) throws Exception {
        final String result = this.restClient.post(router, objects);
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        return JSON.parseObject(result, clazz);
    }

    /**
     * Http post multipart
     *
     * @param router  router
     * @param objects objects
     * @return String
     * @throws Exception Exception
     */
    public final String postMulti(final String router, final Object... objects) throws Exception {
        return this.multiClient.post(router, objects);
    }

    /**
     * Http post multipart
     *
     * @param router  router
     * @param clazz   clazz
     * @param objects objects
     * @param <T>     Any
     * @return T Any
     * @throws Exception Exception
     */
    public final <T> T postMulti(final String router, final Class<T> clazz, final Object... objects) throws Exception {
        final String result = this.multiClient.post(router, objects);
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        return JSON.parseObject(result, clazz);
    }
}
