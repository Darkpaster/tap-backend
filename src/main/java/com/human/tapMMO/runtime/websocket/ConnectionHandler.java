package com.human.tapMMO.runtime.websocket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.human.tapMMO.dto.websocket.ChatMessage;
import com.human.tapMMO.runtime.game.Logger;
import com.human.tapMMO.runtime.game.actors.player.Player;
import com.human.tapMMO.service.game.GameLoopService;
import com.human.tapMMO.service.game.player.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConnectionHandler {

    private final GameLoopService gameLoopService;
    private final PlayerService playerService;
    private final Logger logger;
    @Setter
    private SocketIONamespace generalNamespace;

    public void handleConnect(SocketIOClient client) {
        log.info("Client connected: {}", client.getSessionId());
    }

    public void handleDisconnect(SocketIOClient client) {
        final Player player = getPlayerBySessionId(client.getSessionId());

        if (player != null) {
            handlePlayerDisconnect(player, client);
        } else {
            log.info("Unknown client disconnected: {}", client.getSessionId());
        }
    }

    private Player getPlayerBySessionId(UUID sessionId) {
        final Long playerId = gameLoopService.getPlayerIdList().get(sessionId);
        return playerId != null ? gameLoopService.getPlayerList().get(playerId) : null;
    }

    private void handlePlayerDisconnect(Player player, SocketIOClient client) {
        final ChatMessage disconnectMessage = new ChatMessage(
                -1,
                "system",
                player.getName() + " покинул сервер.",
                ChatMessage.MessageType.SYSTEM
        );

        logger.sendMessage(disconnectMessage);

        if (generalNamespace != null) {
            generalNamespace.getRoomOperations("global")
                    .sendEvent("receiveMessage", disconnectMessage);
        }

        log.info("{} disconnected", player.getName());

        gameLoopService.deletePlayer(player.getId());
        playerService.updateAllCharacterData(player);
    }
}