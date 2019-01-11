package com.xcoder.utilities.http.impl;

import com.xcoder.utilities.http.IStreamBinaryBody;
import org.apache.http.entity.ContentType;

import java.io.InputStream;

/**
 * Stream BinaryBody
 *
 * @author chuck lee.
 */
public class StreamBinaryBody extends AbstractBinaryBody implements IStreamBinaryBody {

    private InputStream stream;

    public StreamBinaryBody() {
        super();
    }

    public StreamBinaryBody(String name) {
        super(name);
    }

    public StreamBinaryBody(String name, String fileName) {
        super(name, fileName);
    }

    public StreamBinaryBody(String name, String fileName, InputStream stream) {
        super(name, fileName);
        this.stream = stream;
    }

    public StreamBinaryBody(String name, String fileName, ContentType contentType) {
        super(name, fileName, contentType);
    }

    public StreamBinaryBody(String name, String fileName, ContentType contentType, InputStream stream) {
        super(name, fileName, contentType);
        this.stream = stream;
    }

    @Override
    public void close() throws Exception {
        if (null != this.stream) {
            this.stream.close();
        }
    }

    @Override
    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }
}
