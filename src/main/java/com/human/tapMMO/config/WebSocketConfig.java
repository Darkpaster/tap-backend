package com.human.tapMMO.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.human.tapMMO.dto.MobDTO;
import com.human.tapMMO.model.*;
import com.human.tapMMO.model.tables.Character;
import com.human.tapMMO.model.tables.Item;
import com.human.tapMMO.model.tables.Mob;
import com.human.tapMMO.service.*;
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

    @Autowired
    private GameLoopService entityManager;

    private RespawnService respawnService;

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
    public Function<List<MobDTO>, List<MobDTO>> sendUpdatedMobs() {
        return input -> {
            generalNamespace.getAllClients().forEach(client -> client.sendEvent("updateAllMobs", input));
            return input;
        };
    }

    @Bean
    public SocketIONamespace socketIONamespace(SocketIOServer socketIOServer) {
        this.generalNamespace = socketIOServer.addNamespace("/ws-general");

        entityManager.init(mobService.initAllMobs(), itemService.initAllItems(), sendUpdatedMobs());

        generalNamespace.addEventListener("sendMessage", ChatMessage.class, (client, data, ackRequest) -> {
            String roomId = getRoomForClient(client);
            client.getNamespace().getRoomOperations(roomId).sendEvent("receiveMessage", data);
            System.out.println("Message sent: " + data.getContent());
        });

        generalNamespace.addEventListener("sendPlayerPosition", Position.class, (client, data, ackRequest) -> {
            String roomId = getRoomForClient(client);
            client.getNamespace().getRoomOperations(roomId).sendEvent("receivePlayerPosition", data);
            entityManager.updatePlayer(data);
        });

        generalNamespace.addEventListener("sendMobPosition", Position.class, (client, data, ackRequest) -> {
            String roomId = getRoomForClient(client);
            client.getNamespace().getRoomOperations(roomId).sendEvent("receiveMobPosition", data);
        });

        generalNamespace.addEventListener("updateVisibleStats", Position.class, (client, data, ackRequest) -> {
            String roomId = getRoomForClient(client);
            client.getNamespace().getRoomOperations(roomId).sendEvent("receiveVisibleStats", data);
        });


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

        generalNamespace.addEventListener("deleteItem", Item.class, (client, data, ackRequest) -> {
            System.out.println("player " + data.getId() + " is deleting");
            String roomId = getRoomForClient(client);
            client.getNamespace().getRoomOperations(roomId)
                    .sendEvent("receiveDeletedItem", data);
        });

        generalNamespace.addEventListener("sendItem", InitCharacterConnection.class, (client, data, ackRequest) -> {
            System.out.println("char " + data.getCharacterId() + " joined to " + data.getRoomId());
            client.joinRoom(data.getRoomId());
            client.getNamespace().getRoomOperations(data.getRoomId())
                    .sendEvent("userConnected", data);
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

        return generalNamespace;
    }

}