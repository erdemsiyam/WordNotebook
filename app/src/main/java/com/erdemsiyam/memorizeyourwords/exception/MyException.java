package com.erdemsiyam.memorizeyourwords.exception;

public class MyException extends RuntimeException {
    public MyException(String message) {super(message);}

    public final static String NO_CONTENT = "Yazı Girmelisiniz.";
    public final static String CONTENT_LIMIT_EXCEEDED = "30 karakter ve aşağısı olmalı.";
}
