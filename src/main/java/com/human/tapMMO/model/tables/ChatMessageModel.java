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

}
