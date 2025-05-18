package com.human.tapMMO.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.human.tapMMO.dto.rest.ItemDTO;
import com.human.tapMMO.dto.websocket.ActorDTO;
import com.human.tapMMO.dto.websocket.DamageDTO;
import com.human.tapMMO.model.connection.InitCharacterConnection;
import com.human.tapMMO.model.connection.InitUserResponse;
import com.human.tapMMO.dto.websocket.ChatMessage;
import com.human.tapMMO.dto.websocket.Position;
import com.human.tapMMO.model.tables.ItemPosition;
import com.human.tapMMO.model.tables.MobModel;
import com.human.tapMMO.repository.ItemRepository;
import com.human.tapMMO.runtime.game.world.MapManager;
import com.human.tapMMO.service.game.GameLoopService;
import com.human.tapMMO.service.game.player.ItemService;
import com.human.tapMMO.service.game.world.MobService;
import com.human.tapMMO.service.game.world.RespawnService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
public class WebSocketConfig {

    private final ItemService itemService;
    private final MobService mobService;

    private final ConcurrentHashMap<String, CompletableFuture<InitCharacterConnection>> pendingRequests = new ConcurrentHashMap<>();

    private final GameLoopService entityManager;

//    @Autowired
    private final RespawnService respawnService;

    private final MapManager mapManager;

    @Value("${socket.host}")
    private String host;

    @Value("${socket.port}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        SocketIOServer server = getSocketIOServer();

        server.start();
        return server;
    }

    private SocketIOServer getSocketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setAllowCustomRequests(true);

        config.setEnableCors(true);

        config.setRandomSession(true);
        config.setMaxFramePayloadLength(1024 * 1024);
        config.setMaxHttpContentLength(1024 * 1024);

        SocketIOServer server = new SocketIOServer(config);


        server.addConnectListener(client -> {
            System.out.println("Client connected: " + client.getSessionId());
        });
        return server;
    }

    private String generateRoomId() {
        return UUID.randomUUID().toString();//.substring(0, 6);
    }

    private String getRoomForClient(SocketIOClient client) {
        Set<String> rooms = client.getAllRooms();
        if (rooms.isEmpty()) {
            return "public";
        }
//        return rooms.iterator().next();
        return "public";
    }

    @PreDestroy
    public void stopSocketIOServer() {
        socketIOServer().stop();
    }


    private SocketIONamespace generalNamespace;

    @Bean
    public Function<List<ActorDTO>, List<ActorDTO>> sendUpdatedMobs() {
        return input -> {
            generalNamespace.getAllClients().forEach(client -> client.sendEvent("updateAllMobs", input));//создать mobDTO для отправки
            return input;
        };
    }

    @Bean
    public SocketIONamespace socketIONamespace(SocketIOServer socketIOServer,
                                               ItemRepository itemRepository) {
        this.generalNamespace = socketIOServer.addNamespace("/ws-general");

        mapManager.init().thenRun(() -> {
            assert !mapManager.actorList.isEmpty();
            mobService.updateDB(mapManager.actorList);
            entityManager.init(mobService.initAllMobs(), itemService.initAllItems(), sendUpdatedMobs());
        });

        generalNamespace.addEventListener("sendMessage", ChatMessage.class, (client, data, ackRequest) -> {
            String roomId = getRoomForClient(client);
            client.getNamespace().getRoomOperations(roomId).sendEvent("receiveMessage", data);
            System.out.println("Message sent: " + data.getContent());
        });

        generalNamespace.addEventListener("sendPlayerPosition", ActorDTO.class, (client, data, ackRequest) -> {
            String roomId = getRoomForClient(client);
            client.getNamespace().getRoomOperations(roomId).sendEvent("receivePlayerPosition", data);
            entityManager.updatePlayer(data);
        });

//        generalNamespace.addEventListener("sendMobPosition", ActorDTO.class, (client, data, ackRequest) -> {
//            String roomId = getRoomForClient(client);
//            client.getNamespace().getRoomOperations(roomId).sendEvent("receiveMobPosition", data);
//        });

//        generalNamespace.addEventListener("updateVisibleStats", Position.class, (client, data, ackRequest) -> {
//            String roomId = getRoomForClient(client);
//            client.getNamespace().getRoomOperations(roomId).sendEvent("receiveVisibleStats", data);
//        });


        generalNamespace.addEventListener("createRoom", String.class, (client, roomId, ackRequest) -> {
            System.out.println("room created: " + roomId);
            client.sendEvent("roomCreated", roomId);
        });


        generalNamespace.addEventListener("joinRoom", InitCharacterConnection.class, (client, data, ackRequest) -> {
            System.out.println("char " + data.getCharacterId() + " joined to " + data.getRoomId());
            client.joinRoom(data.getRoomId());
            client.getNamespace().getRoomOperations(data.getRoomId())
                    .sendEvent("userConnected", data);
            entityManager.addNewPlayer(data);
        });


        generalNamespace.addEventListener("initUsers", InitCharacterConnection.class, (client, data, ackRequest) -> {
            System.out.println("char " + data.getCharacterId() + " requested init users in " + data.getRoomId());
            List<SocketIOClient> users = client.getNamespace().getAllClients().stream().toList();
            int totalUsers = users.size();

            // Создаем массив для CompletableFuture, каждый для одного клиента
            List<CompletableFuture<InitCharacterConnection>> futures = new ArrayList<>(totalUsers);

            for (SocketIOClient user : users) {
                // Создаем новый CompletableFuture для каждого клиента
                CompletableFuture<InitCharacterConnection> future = new CompletableFuture<>();

                // Генерируем уникальный ID для этого запроса
                String requestId = UUID.randomUUID().toString();

                // Сохраняем ссылку на future в каком-то хранилище, связанном с requestId
                pendingRequests.put(requestId, future);

                // Отправляем запрос клиенту с уникальным ID
                user.sendEvent("sendToInitUser", requestId);

                futures.add(future);
            }

            // Когда все futures будут завершены, отправляем ответ исходному клиенту
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenAccept(v -> {
                        // Собираем все результаты
                        InitCharacterConnection[] clientList = new InitCharacterConnection[totalUsers];
                        for (int i = 0; i < totalUsers; i++) {
                            try {
                                clientList[i] = futures.get(i).get();
                            } catch (Exception e) {
                                // Обработка ошибок
                                clientList[i] = null;
                                System.err.println("Error getting client data: " + e.getMessage());
                            }
                        }


                        // Отправляем собранные данные исходному клиенту
                        client.sendEvent("allUsersConnected", (Object) clientList);
                    });
        });

// Обработчик для получения ответов от клиентов
        generalNamespace.addEventListener("initUserResponse", InitUserResponse.class, (client, response, ackRequest) -> {
            String requestId = response.getRequestId();
            CompletableFuture<InitCharacterConnection> future = pendingRequests.remove(requestId);

            if (future != null) {
                // Завершаем future с полученными данными
                future.complete(response.getCharacterData());

                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(5000);

                        // Если future еще не завершен, завершаем его с нулевым результатом
                        if (!future.isDone()) {
                            pendingRequests.remove(requestId);
                            future.complete(null);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        });


        //delete mob & player on server side only

        generalNamespace.addEventListener("deleteItem", ItemPosition.class, (client, data, ackRequest) -> {
            System.out.println("item " + data.getItemId() + " is deleting");
            String roomId = getRoomForClient(client);
            entityManager.deleteItem(data.getItemId());
            itemService.deleteItem(data.getItemId());
            client.getNamespace().getRoomOperations(roomId)
                    .sendEvent("deleteItem", data);
        });

        generalNamespace.addEventListener("lootItem", ItemDTO.class, (client, data, ackRequest) -> {
            System.out.println("item looted " + data.getId());
            String roomId = getRoomForClient(client);
            long posId;
            if (itemService.isItemExist(data.getId())) {
                posId = itemService.dropItem(data);
            } else {
                posId = itemService.lootItem(data);
            }
            entityManager.addNewItem(data, posId);
            client.getNamespace().getRoomOperations(roomId)
                    .sendEvent("addItem", data);
        });

//        generalNamespace.addEventListener("dropItem", ItemPosition.class, (client, data, ackRequest) -> {
//            System.out.println("char sent item " + data.getItemId());
//            String roomId = getRoomForClient(client);
//            entityManager.addNewItem(data);
//            itemService.dropItem(data);
//            client.getNamespace().getRoomOperations(roomId)
//                    .sendEvent("addItem", data);
//        });

        generalNamespace.addEventListener("dealDamage", DamageDTO.class, (client, data, ackrequest) -> {
            System.out.println("dealt damage to " + data.getTarget().getTargetId());
            String roomId = getRoomForClient(client);
            if (Objects.equals(data.getTarget().getTargetType(), "mob")) {
                final var mobId = data.getTarget().getTargetId();
                entityManager.dealDamageToMob(mobId, data.getValue());
            } else {
                System.out.println("удар по игроку");
            }
        });


//        namespace.addEventListener("ice-candidate", Candidate.class, (client, data, ackRequest) -> {
//            System.out.println("ice candidate: "+data.getRoomId());
//            Object candidate = data.getCandidate();
//            String roomId = data.getRoomId();
//            client.getNamespace().getRoomOperations(roomId)
//                    .sendEvent("ice-candidate", candidate);
//        });
//
//        namespace.addEventListener("offer", Object.class, (client, offer, ackRequest) -> {
//            System.out.println("offer "+client.getSessionId());
//            String roomId = getRoomForClient(client);
//            client.getNamespace().getRoomOperations(roomId)
//                    .sendEvent("offer", offer);
//        });
//
//        namespace.addEventListener("answer", Object.class, (client, answer, ackRequest) -> {
//            System.out.println("answer "+client.getSessionId());
//            String roomId = getRoomForClient(client);
//            client.getNamespace().getRoomOperations(roomId)
//                    .sendEvent("answer", answer);
//        });

        respawnService.init(mob -> {
            entityManager.addNewMob(mob);
            return mob;
        });

        return generalNamespace;
    }

}