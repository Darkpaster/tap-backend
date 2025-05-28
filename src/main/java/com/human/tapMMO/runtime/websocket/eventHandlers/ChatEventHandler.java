package com.human.tapMMO.runtime.websocket.eventHandlers;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.human.tapMMO.dto.websocket.ChatMessage;
import com.human.tapMMO.runtime.game.Logger;
import com.human.tapMMO.runtime.websocket.service.WebSocketBroadcastService;
import com.human.tapMMO.runtime.websocket.utils.RoomUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatEventHandler {

    private final Logger logger;
    private final WebSocketBroadcastService broadcastService;

    public void handleSendMessage(SocketIOClient client, ChatMessage data, AckRequest ackRequest) {
        try {
            String roomId = RoomUtils.getRoomForClient(client);

            // Отправляем сообщение в комнату
            broadcastService.broadcastToRoom(roomId, "receiveMessage", data);

            // Логируем сообщение
            logger.sendMessage(data);

            log.debug("Message sent to room {}: {}", roomId, data.getContent());
        } catch (Exception e) {
            log.error("Error handling send message event", e);
        }
    }
}