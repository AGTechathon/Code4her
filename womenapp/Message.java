// File: app/src/main/java/com/example/womenapp/Message.java
package com.example.womenapp;

public class Message {
    private String text;
    private boolean isUser; // true for user's message, false for chatbot's message

    /**
     * Constructor for a chat message.
     * @param text The actual text content of the message.
     * @param isUser True if the message was sent by the user, false if by the chatbot.
     */
    public Message(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
    }

    /**
     * Get the text content of the message.
     * @return The message text.
     */
    public String getText() {
        return text;
    }

    /**
     * Check if the message was sent by the user.
     * @return True if it's a user message, false if it's a bot message.
     */
    public boolean isUser() {
        return isUser;
    }
}
