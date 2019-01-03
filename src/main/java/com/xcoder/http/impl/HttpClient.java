package com.xcoder.http.impl;

import org.apache.http.HttpEntity;

/**
 * Simple http client.
 *
 * @author Chuck Lee.
 */
public class HttpClient extends AbstractHttpClient {

    /**
     * Constructor with server address
     *
     * @param serverAddress ip:port
     */
    public HttpClient(String serverAddress) {
        super(serverAddress);
    }

    @Override
    public HttpEntity getHttpEntity(Object... objects) {
        return getMultipartEntity(objects);
    }
}
