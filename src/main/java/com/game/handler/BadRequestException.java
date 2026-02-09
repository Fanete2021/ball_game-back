package com.game.handler;

public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}