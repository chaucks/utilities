package com.xcoder.http.impl;

import com.xcoder.http.IFileBinaryBody;
import org.apache.http.entity.ContentType;

import java.io.File;

/**
 * File BinaryBody
 *
 * @author chuck lee.
 */
public class FileBinaryBody extends AbstractBinaryBody implements IFileBinaryBody {

    private File file;

    public FileBinaryBody() {
        super();
    }

    public FileBinaryBody(String name) {
        super(name);
    }

    public FileBinaryBody(String name, String fileName) {
        super(name, fileName);
    }

    public FileBinaryBody(String name, String fileName, File file) {
        super(name, fileName);
        this.file = file;
    }

    public FileBinaryBody(String name, String fileName, ContentType contentType) {
        super(name, fileName, contentType);
    }

    public FileBinaryBody(String name, String fileName, ContentType contentType, File file) {
        super(name, fileName, contentType);
        this.file = file;
    }

    @Override
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
