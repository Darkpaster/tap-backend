package com.human.tapMMO.exception;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebSocketExceptionHandler {

    /**
     * Обрабатывает исключения WebSocket и отправляет ошибку клиенту
     */
    public void handleException(SocketIOClient client, String eventName, Exception e) {
        log.error("Error handling WebSocket event '{}' for client {}: {}",
                eventName, client.getSessionId(), e.getMessage(), e);

        // Отправляем клиенту уведомление об ошибке
        sendErrorToClient(client, eventName, e.getMessage());
    }

    /**
     * Обрабатывает критические исключения, которые могут повлиять на соединение
     */
    public void handleCriticalException(SocketIOClient client, String eventName, Exception e) {
        log.error("Critical error handling WebSocket event '{}' for client {}: {}",
                eventName, client.getSessionId(), e.getMessage(), e);

        sendErrorToClient(client, eventName, "Critical server error occurred");

        // При необходимости можно отключить клиента
        // client.disconnect();
    }

    /**
     * Отправляет ошибку клиенту
     */
    private void sendErrorToClient(SocketIOClient client, String eventName, String errorMessage) {
        try {
            client.sendEvent("error", new WebSocketError(eventName, errorMessage));
        } catch (Exception e) {
            log.error("Failed to send error message to client {}: {}",
                    client.getSessionId(), e.getMessage());
        }
    }

    /**
     * Класс для представления ошибки WebSocket
     */
    public static class WebSocketError {
        private final String event;
        private final String message;
        private final long timestamp;

        public WebSocketError(String event, String message) {
            this.event = event;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getEvent() {
            return event;
        }

        public String getMessage() {
            return message;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}