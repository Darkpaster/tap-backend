package com.human.tapMMO.runtime.websocket.eventHandlers;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.human.tapMMO.dto.websocket.ActorDTO;
import com.human.tapMMO.runtime.websocket.service.WebSocketBroadcastService;
import com.human.tapMMO.runtime.websocket.utils.RoomUtils;
import com.human.tapMMO.service.game.GameLoopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerEventHandler {

    private final GameLoopService gameLoopService;
    private final WebSocketBroadcastService broadcastService;

    public void handlePlayerPosition(SocketIOClient client, ActorDTO data, AckRequest ackRequest) {
        try {
            String roomId = RoomUtils.getRoomForClient(client);

            // Обновляем позицию игрока в игровом сервисе
            gameLoopService.updatePlayer(data);

            // Рассылаем обновление позиции другим клиентам в комнате
            broadcastService.broadcastToRoom(roomId, "receivePlayerPosition", data);

            log.debug("Player position updated for actor {}", data.getActorId());
        } catch (Exception e) {
            log.error("Error handling player position update", e);
        }
    }
}