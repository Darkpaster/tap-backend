package com.human.tapMMO.runtime.websocket.service;

import com.corundumstudio.socketio.SocketIONamespace;
import com.human.tapMMO.dto.websocket.ActorDTO;
import com.human.tapMMO.runtime.game.actors.player.Player;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Service
@Slf4j
public class WebSocketBroadcastService {

    private SocketIONamespace generalNamespace;

    public List<ActorDTO> broadcastMobUpdates(List<ActorDTO> mobs) {
        if (generalNamespace != null) {
            generalNamespace.getAllClients()
                    .forEach(client -> client.sendEvent("updateAllMobs", mobs));
        }
        return mobs;
    }

    public Player sendPlayerHealthUpdate(Player player) {
        if (generalNamespace != null && player.getSessionId() != null) {
            var client = generalNamespace.getClient(player.getSessionId());
            if (client != null) {
                client.sendEvent("updatePlayerHealth", player.getHealth());
            }
        }
        return player;
    }

    public Player broadcastPlayerUpdate(Player player) {
        if (generalNamespace != null) {
            generalNamespace.getAllClients()
                    .forEach(client -> client.sendEvent("updatePlayer", player));
        }
        return player;
    }

    public void broadcastToRoom(String room, String event, Object data) {
        if (generalNamespace != null) {
            generalNamespace.getRoomOperations(room).sendEvent(event, data);
        }
    }

    public void broadcastToAll(String event, Object data) {
        if (generalNamespace != null) {
            generalNamespace.getAllClients()
                    .forEach(client -> client.sendEvent(event, data));
        }
    }
}