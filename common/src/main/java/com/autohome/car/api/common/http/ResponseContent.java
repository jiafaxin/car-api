/**
 * ResponseContent.java
 * com.jfly.core.httpclient
 * Copyright (c) 2014.
 */

package com.autohome.car.api.common.http;

import java.io.UnsupportedEncodingException;

/**
 * 封装HttpClient返回数据
 */
public class ResponseContent {

    public ResponseContent() {

    }

    private int returncode;
    private String message;

    public ResponseContent(String encoding) {
        this.encoding = encoding;
    }

    private String encoding;

    private byte[] contentBytes;

    private int statusCode;

    private String contentType;

    private String contentTypeString;

    public int getReturncode() {
        return returncode;
    }

    public void setReturncode(int returncode) {
        this.returncode = returncode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentTypeString() {
        return this.contentTypeString;
    }

    public void setContentTypeString(String contenttypeString) {
        this.contentTypeString = contenttypeString;
    }

    public String getContent() throws UnsupportedEncodingException {
        return this.getContent(this.encoding);
    }

    public String getContent(String encoding) throws UnsupportedEncodingException {
        return new String(contentBytes, encoding);
    }

    public String getUTFContent() throws UnsupportedEncodingException {
        return this.getContent("UTF-8");
    }

    public byte[] getContentBytes() {
        return contentBytes;
    }

    public void setContentBytes(byte[] contentBytes) {
        this.contentBytes = contentBytes;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

}
