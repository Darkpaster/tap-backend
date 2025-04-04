package com.human.tapMMO.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.human.tapMMO.model.InitCharacterConnection;
import com.human.tapMMO.model.InitUserResponse;
import com.human.tapMMO.model.ChatMessage;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@org.springframework.context.annotation.Configuration
public class WebSocketConfig {

    private ConcurrentHashMap<String, CompletableFuture<InitCharacterConnection>> pendingRequests = new ConcurrentHashMap<>();

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


    @Bean
    public SocketIONamespace socketIONamespace(SocketIOServer socketIOServer) {
        SocketIONamespace namespace = socketIOServer.addNamespace("/ws-chat");

        namespace.addEventListener("sendMessage", ChatMessage.class, (client, data, ackRequest) -> {
            String roomId = getRoomForClient(client);
            client.getNamespace().getRoomOperations(roomId).sendEvent("receiveMessage", data);
            System.out.println("Message sent: " + data.getContent());
        });

        namespace.addEventListener("sendPosition", InitCharacterConnection.class, (client, data, ackRequest) -> {
            String roomId = getRoomForClient(client);
            client.getNamespace().getRoomOperations(roomId).sendEvent("receivePosition", data);
        });


        namespace.addEventListener("createRoom", String.class, (client, roomId, ackRequest) -> {
            System.out.println("room created: " + roomId);
            client.sendEvent("roomCreated", roomId);
        });


        namespace.addEventListener("joinRoom", InitCharacterConnection.class, (client, data, ackRequest) -> {
            System.out.println("client " + data.getId() + " joined to " + data.getRoomId());
            client.joinRoom(data.getRoomId());
            client.getNamespace().getRoomOperations(data.getRoomId())
                    .sendEvent("userConnected", data);
        });


        namespace.addEventListener("initUsers", InitCharacterConnection.class, (client, data, ackRequest) -> {
            System.out.println("client " + data.getId() + " requested init users in " + data.getRoomId());
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
                // Например, можно использовать ConcurrentHashMap
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
        namespace.addEventListener("initUserResponse", InitUserResponse.class, (client, response, ackRequest) -> {
            String requestId = response.getRequestId();
            CompletableFuture<InitCharacterConnection> future = pendingRequests.remove(requestId);

            if (future != null) {
                // Завершаем future с полученными данными
                future.complete(response.getCharacterData());

                // В методе addEventListener добавьте для каждого future
//                CompletableFuture.runAsync(() -> {
//                    try {
//                        // Ждем ответа не более 5 секунд
//                        Thread.sleep(5000);
//
//                        // Если future еще не завершен, завершаем его с нулевым результатом
//                        if (!future.isDone()) {
//                            pendingRequests.remove(requestId);
//                            future.complete(null);
//                        }
//                    } catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                    }
//                });
            }
        });


//        namespace.addEventListener("joinRoom", CharacterDTO.class, (client, data, ackRequest) -> {
//            System.out.println("client "+data.getId()+" joined to "+data.getRoomID());
//            client.joinRoom(data.getRoomID());
//            client.getNamespace().getRoomOperations(data.getRoomID())
//                    .sendEvent("userConnected", data);
//        });

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

        return namespace;
    }

}