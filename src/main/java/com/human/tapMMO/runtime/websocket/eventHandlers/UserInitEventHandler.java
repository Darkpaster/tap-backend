package com.human.tapMMO.runtime.websocket.eventHandlers;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.human.tapMMO.model.connection.InitCharacterConnection;
import com.human.tapMMO.model.connection.InitUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserInitEventHandler {

    private final ConcurrentHashMap<String, CompletableFuture<InitCharacterConnection>> pendingRequests = new ConcurrentHashMap<>();
    private static final int RESPONSE_TIMEOUT_SECONDS = 5;

    public void handleInitUsers(SocketIOClient client, InitCharacterConnection data, AckRequest ackRequest) {
        try {
            log.info("Character {} requested init users in room {}", data.getCharacterId(), data.getRoomId());

            List<SocketIOClient> users = client.getNamespace().getAllClients().stream().toList();
            int totalUsers = users.size();

            if (totalUsers == 0) {
                client.sendEvent("allUsersConnected", new InitCharacterConnection[0]);
                return;
            }

            List<CompletableFuture<InitCharacterConnection>> futures = createUserInitFutures(users);

            // Ожидаем завершения всех запросов с таймаутом
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .orTimeout(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .thenAccept(v -> sendAllUsersResponse(client, futures, totalUsers))
                    .exceptionally(throwable -> {
                        log.error("Error or timeout during user initialization", throwable);
                        sendAllUsersResponse(client, futures, totalUsers);
                        return null;
                    });

        } catch (Exception e) {
            log.error("Error handling init users event", e);
        }
    }

    public void handleInitUserResponse(SocketIOClient client, InitUserResponse response, AckRequest ackRequest) {
        try {
            String requestId = response.getRequestId();
            CompletableFuture<InitCharacterConnection> future = pendingRequests.remove(requestId);

            if (future != null && !future.isDone()) {
                future.complete(response.getCharacterData());
                log.debug("Completed user init request: {}", requestId);
            } else {
                log.warn("Received response for unknown or completed request: {}", requestId);
            }

        } catch (Exception e) {
            log.error("Error handling init user response", e);
        }
    }

    private List<CompletableFuture<InitCharacterConnection>> createUserInitFutures(List<SocketIOClient> users) {
        List<CompletableFuture<InitCharacterConnection>> futures = new ArrayList<>(users.size());

        for (SocketIOClient user : users) {
            CompletableFuture<InitCharacterConnection> future = new CompletableFuture<>();
            String requestId = UUID.randomUUID().toString();

            // Сохраняем future с таймаутом
            pendingRequests.put(requestId, future);

            // Устанавливаем таймаут для отдельного запроса
            CompletableFuture.delayedExecutor(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .execute(() -> {
                        CompletableFuture<InitCharacterConnection> timeoutFuture = pendingRequests.remove(requestId);
                        if (timeoutFuture != null && !timeoutFuture.isDone()) {
                            timeoutFuture.complete(null);
                            log.warn("Request {} timed out", requestId);
                        }
                    });

            // Отправляем запрос клиенту
            user.sendEvent("sendToInitUser", requestId);
            futures.add(future);
        }

        return futures;
    }

    private void sendAllUsersResponse(SocketIOClient client, List<CompletableFuture<InitCharacterConnection>> futures, int totalUsers) {
        InitCharacterConnection[] clientList = new InitCharacterConnection[totalUsers];

        for (int i = 0; i < totalUsers; i++) {
            try {
                clientList[i] = futures.get(i).get();
            } catch (Exception e) {
                log.warn("Error getting client data for index {}: {}", i, e.getMessage());
                clientList[i] = null;
            }
        }

        client.sendEvent("allUsersConnected", (Object) clientList);
        log.info("Sent all users data to client, {} users processed", totalUsers);
    }
}