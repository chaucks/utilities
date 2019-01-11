package com.xcoder.utilities.http.impl;

import com.xcoder.utilities.http.IBinaryBody;
import org.apache.http.entity.ContentType;

/**
 * Abstract BinaryBody
 *
 * @author chuck lee.
 */
public abstract class AbstractBinaryBody implements IBinaryBody {
    private String name;

    private String fileName;

    private ContentType contentType;

    public AbstractBinaryBody() {
        this.contentType = MULTIPART_FORM_DATA;
    }

    public AbstractBinaryBody(String name) {
        this();
        this.name = name;
    }

    public AbstractBinaryBody(String name, String fileName) {
        this(name);
        this.fileName = fileName;
    }

    public AbstractBinaryBody(String name, String fileName, ContentType contentType) {
        this(name, fileName);
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }
}
