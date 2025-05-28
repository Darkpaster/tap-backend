package com.human.tapMMO.runtime.websocket.utils;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.UUID;

@UtilityClass
@Slf4j
public class RoomUtils {

    private static final String DEFAULT_ROOM = "global";

    /**
     * Получает комнату для клиента. Если клиент не находится ни в одной комнате,
     * возвращает комнату по умолчанию.
     */
    public static String getRoomForClient(SocketIOClient client) {
        Set<String> rooms = client.getAllRooms();

        if (rooms.isEmpty()) {
            log.debug("Client {} is not in any room, returning default room", client.getSessionId());
            return DEFAULT_ROOM;
        }

        // Возвращаем первую комнату или глобальную, если она есть
        if (rooms.contains(DEFAULT_ROOM)) {
            return DEFAULT_ROOM;
        }

        return rooms.iterator().next();
    }

    /**
     * Генерирует уникальный ID комнаты
     */
    public static String generateRoomId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Проверяет, является ли комната глобальной
     */
    public static boolean isGlobalRoom(String roomId) {
        return DEFAULT_ROOM.equals(roomId);
    }

    /**
     * Получает название комнаты по умолчанию
     */
    public static String getDefaultRoom() {
        return DEFAULT_ROOM;
    }
}