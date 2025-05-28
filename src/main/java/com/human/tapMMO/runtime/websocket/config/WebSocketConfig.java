package com.human.tapMMO.runtime.websocket.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.human.tapMMO.runtime.websocket.ConnectionHandler;
import com.human.tapMMO.runtime.websocket.EventHandlerRegistry;
import com.human.tapMMO.runtime.websocket.service.WebSocketEventService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
public class WebSocketConfig {

    private final ConnectionHandler connectionHandler;
    private final EventHandlerRegistry eventHandlerRegistry;
    private final WebSocketEventService webSocketEventService;
    private final WebSocketConfigProperties configProperties;

    private SocketIOServer server;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = createSocketIOConfiguration();
        server = new SocketIOServer(config);

        // Регистрируем обработчики подключения и отключения
        server.addConnectListener(connectionHandler::handleConnect);
        server.addDisconnectListener(connectionHandler::handleDisconnect);

        server.start();
        return server;
    }

    @Bean
    public SocketIONamespace socketIONamespace(SocketIOServer socketIOServer) {
        SocketIONamespace namespace = socketIOServer.addNamespace("/ws-general");

        // Инициализируем игровые сервисы
        webSocketEventService.initializeGameServices();

        // Регистрируем все обработчики событий
        eventHandlerRegistry.registerAllHandlers(namespace);

        return namespace;
    }

    private Configuration createSocketIOConfiguration() {
        Configuration config = new Configuration();
        config.setHostname(configProperties.getHost());
        config.setPort(configProperties.getPort());
        config.setAllowCustomRequests(configProperties.getAllowCustomRequests());
        config.setEnableCors(configProperties.getCorsEnabled());
        config.setRandomSession(configProperties.getRandomSession());
        config.setMaxFramePayloadLength(configProperties.getMaxFramePayloadLength());
        config.setMaxHttpContentLength(configProperties.getMaxHttpContentLength());
        return config;
    }

    @PreDestroy
    public void stopSocketIOServer() {
        if (server != null) {
            server.stop();
        }
    }
}