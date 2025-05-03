package com.nhnacademy.springtxlab.exception;

public class ExcessivePointIncreaseException extends RuntimeException {
    public ExcessivePointIncreaseException(long amount) {
        super("너무 많은 포인트 증가 요청: " + amount);
    }
}