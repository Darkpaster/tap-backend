package com.human.tapMMO.dto.websocket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private Long senderId;
    private String content;
    private String sender;
    private MessageType type = MessageType.DEFAULT;
    private String roomId = "global";
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonIgnore
    private LocalDateTime timestamp = LocalDateTime.now();

    public ChatMessage(long senderId, String sender, String content) {
        this.senderId = senderId;
        this.sender = sender;
        this.content = content;
    }

    public ChatMessage(long senderId, String sender, String content, String roomId) {
        this.senderId = senderId;
        this.sender = sender;
        this.content = content;
        this.roomId = roomId;
    }

    public ChatMessage(long senderId, String sender, String content, MessageType type) {
        this.senderId = senderId;
        this.sender = sender;
        this.content = content;
        this.type = type;
    }

    public ChatMessage(long senderId, String sender, String content, MessageType type, String roomId) {
        this.senderId = senderId;
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.roomId = roomId;
    }

    public ChatMessage(){

    }

    public enum MessageType {
        DEFAULT, ADMIN, SYSTEM
    }
}