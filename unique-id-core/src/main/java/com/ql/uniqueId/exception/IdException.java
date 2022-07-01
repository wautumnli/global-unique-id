package com.ql.uniqueId.exception;

/**
 * @author wanqiuli
 * @date 2022/7/1 14:45
 */
public class IdException extends RuntimeException{

    private int code;
    private String msg;

    public IdException() {
        super();
    }

    public IdException(String msg) {
        super(msg);
    }

    public IdException(Throwable throwable) {
        super(throwable);
    }

    public IdException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
