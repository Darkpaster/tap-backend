package com.human.tapMMO.runtime.websocket.eventHandlers;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.human.tapMMO.dto.websocket.ChatMessage;
import com.human.tapMMO.model.connection.InitCharacterConnection;
import com.human.tapMMO.runtime.game.Logger;
import com.human.tapMMO.runtime.game.actors.player.Player;
import com.human.tapMMO.runtime.websocket.service.WebSocketBroadcastService;
import com.human.tapMMO.service.game.GameLoopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomEventHandler {

    private final GameLoopService gameLoopService;
    private final Logger logger;
    private final WebSocketBroadcastService broadcastService;

    public void handleCreateRoom(SocketIOClient client, String roomId, AckRequest ackRequest) {
        try {
            log.info("Room created: {}", roomId);
            client.sendEvent("roomCreated", roomId);
        } catch (Exception e) {
            log.error("Error creating room", e);
        }
    }

    public void handleJoinRoom(SocketIOClient client, InitCharacterConnection data, AckRequest ackRequest) {
        try {
            log.info("Character {} joined room {}", data.getCharacterId(), data.getRoomId());

            client.joinRoom(data.getRoomId());
            broadcastService.broadcastToRoom(data.getRoomId(), "userConnected", data);

            if ("global".equals(data.getRoomId())) {
                handleGlobalRoomJoin(client, data);
            } else {
                log.warn("Unexpected room join: {}", data.getRoomId());
            }
        } catch (Exception e) {
            log.error("Error handling room join", e);
        }
    }

    private void handleGlobalRoomJoin(SocketIOClient client, InitCharacterConnection data) {
        final Player player = gameLoopService.getPlayerList().get(data.getCharacterId());

        if (player == null) {
            log.warn("Player not found for character ID: {}", data.getCharacterId());
            return;
        }

        player.setSessionId(client.getSessionId());
        gameLoopService.getPlayerIdList().put(client.getSessionId(), data.getCharacterId());

        final ChatMessage joinMessage = new ChatMessage(
                -1,
                "system",
                player.getName() + " присоединился к серверу.",
                ChatMessage.MessageType.SYSTEM
        );

        logger.sendMessage(joinMessage);
        broadcastService.broadcastToRoom("global", "receiveMessage", joinMessage);

        log.info("Player {} joined global room", player.getName());
    }
}