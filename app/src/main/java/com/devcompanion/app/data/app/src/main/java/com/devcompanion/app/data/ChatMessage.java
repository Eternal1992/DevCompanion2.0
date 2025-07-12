
package com.devcompanion.app.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_messages")
public class ChatMessage {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String message;
    public String sender; // "user" or "ai"
    public long timestamp;
    public String attachmentUri; // optional
    public String attachmentType; // "image", "code", etc.

    public ChatMessage(String message, String sender, long timestamp, String attachmentUri, String attachmentType) {
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
        this.attachmentUri = attachmentUri;
        this.attachmentType = attachmentType;
    }
}
