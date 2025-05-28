package com.human.tapMMO.runtime.websocket.eventHandlers;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.human.tapMMO.dto.rest.ItemDTO;
import com.human.tapMMO.model.tables.ItemPosition;
import com.human.tapMMO.runtime.websocket.service.WebSocketBroadcastService;
import com.human.tapMMO.runtime.websocket.utils.RoomUtils;
import com.human.tapMMO.service.game.GameLoopService;
import com.human.tapMMO.service.game.player.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemEventHandler {

    private final GameLoopService gameLoopService;
    private final ItemService itemService;
    private final WebSocketBroadcastService broadcastService;

    public void handleDeleteItem(SocketIOClient client, ItemPosition data, AckRequest ackRequest) {
        try {
            log.info("Deleting item: {}", data.getItemId());

            String roomId = RoomUtils.getRoomForClient(client);

            // Удаляем предмет из игрового цикла и базы данных
            gameLoopService.deleteItem(data.getItemId());
            itemService.deleteItem(data.getItemId());

            // Уведомляем других клиентов об удалении предмета
            broadcastService.broadcastToRoom(roomId, "deleteItem", data);

            log.debug("Item {} deleted successfully", data.getItemId());
        } catch (Exception e) {
            log.error("Error deleting item: {}", data.getItemId(), e);
        }
    }

    public void handleLootItem(SocketIOClient client, ItemDTO data, AckRequest ackRequest) {
        try {
            log.info("Item looted: {}", data.getId());

            String roomId = RoomUtils.getRoomForClient(client);
            long posId;

            // Обрабатываем предмет в зависимости от того, существует ли он
            if (itemService.isItemExist(data.getId())) {
                posId = itemService.dropItem(data);
                log.debug("Existing item {} dropped at position {}", data.getId(), posId);
            } else {
                posId = itemService.lootItem(data);
                log.debug("New item {} looted at position {}", data.getId(), posId);
            }

            // Добавляем предмет в игровой цикл
            gameLoopService.addNewItem(data, posId);

            // Уведомляем других клиентов о новом предмете
            broadcastService.broadcastToRoom(roomId, "addItem", data);

        } catch (Exception e) {
            log.error("Error handling loot item: {}", data.getId(), e);
        }
    }
}