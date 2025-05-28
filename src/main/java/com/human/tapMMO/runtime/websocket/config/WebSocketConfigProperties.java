package com.human.tapMMO.runtime.websocket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "websocket")
@Data
public class WebSocketConfigProperties {

    /**
     * Хост для WebSocket сервера
     */
    private String host = "localhost";

    /**
     * Порт для WebSocket сервера
     */
    private Integer port = 8050;

    /**
     * Максимальный размер payload для фрейма
     */
    private Integer maxFramePayloadLength = 1024 * 1024;

    /**
     * Максимальный размер HTTP content
     */
    private Integer maxHttpContentLength = 1024 * 1024;

    /**
     * Включить CORS
     */
    private Boolean corsEnabled = true;

    /**
     * Использовать случайные сессии
     */
    private Boolean randomSession = true;

    /**
     * Разрешить кастомные запросы
     */
    private Boolean allowCustomRequests = true;

    /**
     * Таймаут для инициализации пользователей (в секундах)
     */
    private Integer userInitTimeoutSeconds = 5;

    /**
     * Максимальное количество попыток переподключения
     */
    private Integer maxReconnectAttempts = 3;

    /**
     * Интервал между попытками переподключения (в миллисекундах)
     */
    private Long reconnectInterval = 1000L;
}