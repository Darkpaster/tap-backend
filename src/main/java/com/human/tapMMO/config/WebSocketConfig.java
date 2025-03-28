package com.human.tapMMO.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.human.tapMMO.model.ChatMessage;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@org.springframework.context.annotation.Configuration
public class WebSocketConfig {

    @Value("${socket.host}")
    private String host;

    @Value("${socket.port}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        SocketIOServer server = getSocketIOServer();


        // Обработчики WebRTC сигнализации (события для частых данных)
//        server.addEventListener("create-room", String.class, (client, roomId, ackRequest) -> {
//            String newRoomId = generateRoomId();
//            client.joinRoom(newRoomId);
//            client.sendEvent("room-created", newRoomId);
//        });
//
//        server.addEventListener("join-room", String.class, (client, roomId, ackRequest) -> {
//            client.joinRoom(roomId);
//            client.getNamespace().getRoomOperations(roomId)
//                    .sendEvent("user-connected", client.getSessionId().toString());
//        });
//
//        server.addEventListener("ice-candidate", Object.class, (client, candidate, ackRequest) -> {
//            String roomId = getRoomForClient(client);
//            client.getNamespace().getRoomOperations(roomId)
//                    .sendEvent("ice-candidate", candidate);
//        });
//
//        server.addEventListener("offer", Object.class, (client, offer, ackRequest) -> {
//            String roomId = getRoomForClient(client);
//            client.getNamespace().getRoomOperations(roomId)
//                    .sendEvent("offer", offer);
//        });
//
//        server.addEventListener("answer", Object.class, (client, answer, ackRequest) -> {
//            String roomId = getRoomForClient(client);
//            client.getNamespace().getRoomOperations(roomId)
//                    .sendEvent("answer", answer);
//        });

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
            client.joinRoom("public");
        });
        return server;
    }

    private String generateRoomId() {
        return UUID.randomUUID().toString();//.substring(0, 6);
    }

    private String getRoomForClient(SocketIOClient client) {
        Set<String> rooms = client.getAllRooms();
        if (rooms.isEmpty()) {
            return "public"; // возвращаем дефолтную комнату, если нет других
        }
        return rooms.iterator().next(); // иначе берем первую комнату
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
            namespace.getRoomOperations(roomId).sendEvent("receiveMessage", data);
            System.out.println("Message sent: "+data.getContent());
        });

//        namespace.addEventListener("sendMessage", ChatMessage.class, (client, data, ackRequest) -> {
//            System.out.println("SendMessage event received in namespace!");
//            System.out.println("Client: " + client.getSessionId());
//            System.out.println("Data: " + data);
//
//            try {
////                data.setTimestamp(LocalDateTime.now());
//                socketIOServer.getBroadcastOperations().sendEvent("receiveMessage", data);
//                System.out.println("Message sent successfully: " + data.getContent());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });

        return namespace;
    }

//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic"); // Префикс для отправки сообщений
//        config.setApplicationDestinationPrefixes("/app"); // Префикс для получения сообщений
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws-chat")
//                .setAllowedOriginPatterns("*")
//                .withSockJS(); // Поддержка fallback для старых браузеров
//    }
}