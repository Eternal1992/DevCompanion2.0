package com.ethereon.app;

public class ChatMessage {

    public enum Type {
        USER,
        AI
    }

    private final String message;
    private final Type type;

    public ChatMessage(String message, Type type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public Type getType() {
        return type;
    }
}