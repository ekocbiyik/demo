package com.ekocbiyik.demo.exceptions;

/**
 * ekocbiyik on 11.01.2020
 */
public class DemoException extends Exception {

    private int code = -1;

    public DemoException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
