package com.erdemsiyam.memorizeyourwords.exception;

public class MyException extends RuntimeException {
    public MyException(String message) {super(message);}

    public final static String NO_CONTENT = "Yazı Girmelisiniz.";
}
