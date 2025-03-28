package com.human.tapMMO.controller;

import com.corundumstudio.socketio.SocketIOServer;
import com.human.tapMMO.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Controller("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final SocketIOServer socketIOServer;

    @PostMapping("/sendMessage")
    public void sendMessage(@RequestBody ChatMessage message) {
        socketIOServer.getBroadcastOperations().sendEvent("sendMessage", message);
    }

//    @MessageMapping("/sendMessage")
//    @SendTo("/topic/public")
//    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
//        chatMessage.setTimestamp(LocalDateTime.now());
//        return chatMessage;
//    }

//    @MessageMapping("/sendMessage")
//    public void sendMessage(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
//        chatMessage.setTimestamp(LocalDateTime.now());
//        // Здесь используйте Socket.IO для отправки
//        socketIOServer.("public", chatMessage); //ебаный socket.io это не spring websocket (узнай стоит ли использовать web-rtc для передачи координат, а ещё узнай почему не работает cors фильтр по эндпоинтам)
//    }

//    @MessageMapping("/addUser")
//    @SendTo("/topic/public")
//    public ChatMessage addUser(@Payload ChatMessage chatMessage) {
////        chatMessage.setContent(chatMessage.getSender() + " зашёл в игровой мир!");
////        chatMessage.setType(ChatMessage.MessageType.JOIN);
//        return chatMessage;
//    }
}