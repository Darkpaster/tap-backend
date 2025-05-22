package com.human.tapMMO.model.tables;

import com.human.tapMMO.dto.websocket.ChatMessage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "chat_messages")
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(nullable = false, length = 100)
    private String sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatMessage.MessageType type;

    @Column(nullable = false, length = 100)
    private String roomId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public ChatMessageModel(ChatMessage chatMessage) {
        this.senderId = chatMessage.getSenderId();
        this.content = chatMessage.getContent();
        this.sender = chatMessage.getSender();
        this.type = chatMessage.getType();
        this.roomId = chatMessage.getRoomId();
        this.timestamp = chatMessage.getTimestamp();
    }

    public ChatMessage toChatMessage() {
        ChatMessage message = new ChatMessage();
        message.setSenderId(this.senderId);
        message.setContent(this.content);
        message.setSender(this.sender);
        message.setType(this.type);
        message.setRoomId(this.roomId);
        message.setTimestamp(this.timestamp);
        return message;
    }
}
