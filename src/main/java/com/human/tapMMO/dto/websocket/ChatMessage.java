package com.human.tapMMO.dto.websocket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long senderId;
    private String content;
    private String sender;
    private MessageType type = MessageType.DEFAULT;
    private String roomId;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonIgnore
    private LocalDateTime timestamp;

    public enum MessageType {
        DEFAULT, ADMIN, SYSTEM
    }
}