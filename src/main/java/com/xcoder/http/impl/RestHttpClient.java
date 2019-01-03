package com.xcoder.http.impl;

import org.apache.http.HttpEntity;

/**
 * Rest http client.
 *
 * @author Chuck Lee.
 */
public class RestHttpClient extends AbstractHttpClient {

    /**
     * Constructor with server address
     *
     * @param serverAddress ip:port
     */
    public RestHttpClient(String serverAddress) {
        super(serverAddress);
    }

    @Override
    public HttpEntity getHttpEntity(Object... objects) {
        return getStringEntity(objects);
    }
}
