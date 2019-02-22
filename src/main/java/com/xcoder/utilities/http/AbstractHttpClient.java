package com.xcoder.utilities.http;

import com.xcoder.utilities.IUniversal;
import com.xcoder.utilities.common.MixedUtensil;
import com.xcoder.utilities.http.rs.IStreamBinaryBody;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Abstract http client
 *
 * @author Chuck Lee
 */
abstract class AbstractHttpClient implements IUniversal, AutoCloseable {

    /**
     * Http server address(ip:port) empty error message.
     */
    private static final String SERVER_ADDRESS_EMPTY_ERROR_MESSAGE = "Http server address can not be null. Please check...";

    /**
     * 文件流暂存，释放资源用
     */
    private static final ThreadLocal<Collection<AutoCloseable>> AUTO_CLOSEABLE_CACHE = new ThreadLocal<>();

    /**
     * Http server address
     */
    private final String serverAddress;

    /**
     * Constructor
     *
     * @param serverAddress serverAddress can not be empty
     */
    AbstractHttpClient(final String serverAddress) {
        MixedUtensil.stringEmptyRuntimeException(serverAddress, SERVER_ADDRESS_EMPTY_ERROR_MESSAGE);
        this.serverAddress = serverAddress;
    }

    /**
     * Get http header content type
     *
     * @return String
     */
    public abstract String getContentType();

    /**
     * Get http entry
     *
     * @param objects objects
     * @return HttpEntity
     */
    public abstract HttpEntity getHttpEntity(final Object... objects);

    /**
     * Get charset
     *
     * @return Charset
     */
    public abstract Charset getCharset();

    /**
     * Http get
     *
     * @param router router
     * @return String
     * @throws Exception Exception
     */
    protected final String get(final String router) throws Exception {
        final String url = this.getUrl(router);
        final HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(HTTP.CONTENT_TYPE, this.getContentType());
        return this.execute(httpGet);
    }

    /**
     * Http post
     *
     * @param router  router
     * @param objects objects
     * @return String
     * @throws Exception Exception
     */
    final String post(final String router, final Object... objects) throws Exception {
        final String url = this.getUrl(router);
        final HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_TYPE, this.getContentType());
        httpPost.setEntity(this.getHttpEntity(objects));
        return this.execute(httpPost);
    }

    /**
     * Execute http request
     *
     * @param hrb hrb
     * @return String
     * @throws Exception Exception
     */
    private String execute(final HttpRequestBase hrb) throws Exception {
        try (final CloseableHttpClient chc = HttpClients.createDefault();
             final CloseableHttpResponse chr = chc.execute(hrb);
             final AutoCloseable ac = this) {
            final StatusLine sl = chr.getStatusLine();
            final int sc = sl.getStatusCode();
            if (HttpStatus.SC_OK == sc) {
                final Charset cs = this.getCharset();
                final HttpEntity he = chr.getEntity();
                final String rst = EntityUtils.toString(he, cs);
                return rst;
            }
            return null;
        }
    }

    /**
     * Close or release resources
     *
     * @throws Exception Exception
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
     * Get http url
     *
     * @param router router
     * @return String
     */
    private String getUrl(final String router) {
        String url = this.serverAddress;
        if (StringUtils.isNotEmpty(router)) {
            url = this.serverAddress + router;
        }
        return url;
    }

    /**
     * 缓存文件流body
     *
     * @param body body
     */
    static void cacheIStreamBinaryBody(final IStreamBinaryBody body) {
        if (null == AUTO_CLOSEABLE_CACHE.get()) {
            AUTO_CLOSEABLE_CACHE.set(new ArrayList<>(4));
        }
        AUTO_CLOSEABLE_CACHE.get().add(body);
    }
}
