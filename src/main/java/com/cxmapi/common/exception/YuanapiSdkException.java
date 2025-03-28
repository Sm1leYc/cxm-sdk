package com.cxmapi.common.exception;

public class YuanapiSdkException extends Exception{

    public int errorCode;

    public YuanapiSdkException(String message) {
        super(message);
    }

    public YuanapiSdkException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
