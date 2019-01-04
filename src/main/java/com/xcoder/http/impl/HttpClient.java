package com.xcoder.http.impl;

import com.xcoder.utilities.MixedUtensil;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Simple http client.
 *
 * @author Chuck Lee.
 */
public class HttpClient extends AbstractHttpClient {

    /**
     * Http server address(ip:port) empty error message.
     */
    private static final String SERVER_ADDRESS_EMPTY_ERROR_MESSAGE = "Http server address can not be null. Please check...";

    /**
     * Http server address(ip:port)
     */
    private String serverAddress;

    /**
     * Constructor
     */
    public HttpClient() {
    }

    /**
     * Constructor with server address(ip:port)
     *
     * @param serverAddress ip:port
     */
    public HttpClient(final String serverAddress) {
        MixedUtensil.stringEmptyRuntimeException(serverAddress, SERVER_ADDRESS_EMPTY_ERROR_MESSAGE);
        this.serverAddress = serverAddress;
    }

    @Override
    public HttpRequestBase getHttpRequestBase(String url, Object... objects) {
        return getHttpPost(url, objects);
    }

    /**
     * Rest http post request and response.
     *
     * @param url     url
     * @param clazz   clazz
     * @param objects objects
     * @param <T>     T
     * @return
     * @throws Exception
     */
    public <T> T postRest(final String url, final Class<T> clazz, final Object... objects) throws Exception {
        final String requestUri = MixedUtensil.appendString(this.serverAddress, url);
        return AbstractHttpClient.DEFAULT_POST_CLIENT_REST.getResult(requestUri, clazz, objects);
    }

    /**
     * Http post request and response.
     *
     * @param url     url
     * @param clazz   clazz
     * @param objects objects
     * @param <T>     T
     * @return
     * @throws Exception
     */
    public <T> T post(final String url, final Class<T> clazz, final Object... objects) throws Exception {
        final String requestUri = MixedUtensil.appendString(this.serverAddress, url);
        return AbstractHttpClient.DEFAULT_POST_CLIENT.getResult(requestUri, clazz, objects);
    }

    /**
     * Get http server address(ip:port)
     *
     * @return
     */
    public String getServerAddress() {
        return this.serverAddress;
    }

    /**
     * Set http server address(ip:port)
     *
     * @param serverAddress ip:port
     */
    public void setServerAddress(final String serverAddress) {
        MixedUtensil.stringEmptyRuntimeException(serverAddress, SERVER_ADDRESS_EMPTY_ERROR_MESSAGE);
        this.serverAddress = serverAddress;
    }

}
