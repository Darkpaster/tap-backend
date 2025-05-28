package com.human.tapMMO.runtime.websocket;

import com.corundumstudio.socketio.SocketIONamespace;
import com.human.tapMMO.runtime.websocket.eventHandlers.*;
import com.human.tapMMO.runtime.websocket.service.WebSocketBroadcastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventHandlerRegistry {

    private final ChatEventHandler chatEventHandler;
    private final PlayerEventHandler playerEventHandler;
    private final RoomEventHandler roomEventHandler;
    private final ItemEventHandler itemEventHandler;
    private final CombatEventHandler combatEventHandler;
    private final UserInitEventHandler userInitEventHandler;
    private final ConnectionHandler connectionHandler;
    private final WebSocketBroadcastService broadcastService;

    public void registerAllHandlers(SocketIONamespace namespace) {
        // Устанавливаем namespace для всех сервисов
        broadcastService.setGeneralNamespace(namespace);
        connectionHandler.setGeneralNamespace(namespace);

        // Регистрируем обработчики событий чата
        registerChatHandlers(namespace);

        // Регистрируем обработчики событий игрока
        registerPlayerHandlers(namespace);

        // Регистрируем обработчики комнат
        registerRoomHandlers(namespace);

        // Регистрируем обработчики предметов
        registerItemHandlers(namespace);

        // Регистрируем обработчики боя
        registerCombatHandlers(namespace);

        // Регистрируем обработчики инициализации пользователей
        registerUserInitHandlers(namespace);

        log.info("All WebSocket event handlers registered successfully");
    }

    private void registerChatHandlers(SocketIONamespace namespace) {
        namespace.addEventListener("sendMessage",
                com.human.tapMMO.dto.websocket.ChatMessage.class,
                chatEventHandler::handleSendMessage);
    }

    private void registerPlayerHandlers(SocketIONamespace namespace) {
        namespace.addEventListener("sendPlayerPosition",
                com.human.tapMMO.dto.websocket.ActorDTO.class,
                playerEventHandler::handlePlayerPosition);
    }

    private void registerRoomHandlers(SocketIONamespace namespace) {
        namespace.addEventListener("createRoom",
                String.class,
                roomEventHandler::handleCreateRoom);

        namespace.addEventListener("joinRoom",
                com.human.tapMMO.model.connection.InitCharacterConnection.class,
                roomEventHandler::handleJoinRoom);
    }

    private void registerItemHandlers(SocketIONamespace namespace) {
        namespace.addEventListener("deleteItem",
                com.human.tapMMO.model.tables.ItemPosition.class,
                itemEventHandler::handleDeleteItem);

        namespace.addEventListener("lootItem",
                com.human.tapMMO.dto.rest.ItemDTO.class,
                itemEventHandler::handleLootItem);
    }

    private void registerCombatHandlers(SocketIONamespace namespace) {
        namespace.addEventListener("dealDamage",
                com.human.tapMMO.dto.websocket.DamageDTO.class,
                combatEventHandler::handleDealDamage);
    }

    private void registerUserInitHandlers(SocketIONamespace namespace) {
        namespace.addEventListener("initUsers",
                com.human.tapMMO.model.connection.InitCharacterConnection.class,
                userInitEventHandler::handleInitUsers);

        namespace.addEventListener("initUserResponse",
                com.human.tapMMO.model.connection.InitUserResponse.class,
                userInitEventHandler::handleInitUserResponse);
    }
}