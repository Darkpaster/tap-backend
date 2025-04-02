package com.human.tapMMO.controller;

import com.corundumstudio.socketio.SocketIOServer;
import com.human.tapMMO.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final SocketIOServer socketIOServer;

    @PostMapping("/sendMessage")
    public void sendMessage(@RequestBody ChatMessage message) {
        socketIOServer.getBroadcastOperations().sendEvent("sendMessage", message);
    }
}