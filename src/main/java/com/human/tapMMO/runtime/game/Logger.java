package com.human.tapMMO.runtime.game;

import com.human.tapMMO.dto.websocket.ChatMessage;
import com.human.tapMMO.mapper.ChatMessageMapper;
import com.human.tapMMO.model.tables.ChatMessageModel;
import com.human.tapMMO.repository.ChatMessageRepository;
import com.human.tapMMO.runtime.game.actors.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Slf4j
@Component
public class Logger {
    private static final int MAX_MESSAGES_IN_MEMORY = 1000;
    private static final int KEEP_MESSAGES_IN_MEMORY = 100;
    private static final String LOG_FILE_EXTENSION = ".txt";
    private static final String DEFAULT_ROOM_ID = "global";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final Map<Long, Player> playerList;
    private final Map<String, List<ChatMessage>> messageHistoryByRoom;
    private final Map<String, ReadWriteLock> locksByRoom;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;

    @Value("${chat.logger.use-database:true}")
    private boolean useDatabaseStorage;

    @Value("${chat.logger.use-file-backup:false}")
    private boolean useFileBackup;

    public Logger(Map<Long, Player> playerList, @Autowired(required = false) ChatMessageRepository chatMessageRepository, ChatMessageMapper chatMessageMapper) {
        this.playerList = playerList;
        this.messageHistoryByRoom = new ConcurrentHashMap<>();
        this.locksByRoom = new ConcurrentHashMap<>();
        this.chatMessageRepository = chatMessageRepository;
        this.chatMessageMapper = chatMessageMapper;
    }

    /**
     * Отправляет сообщение и сохраняет его в истории комнаты
     */
    public void sendMessage(ChatMessage chatMessage) {
        // Устанавливаем timestamp если он не установлен
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(LocalDateTime.now());
        }

        // Получаем имя отправителя из списка игроков если не указано
        if (chatMessage.getSender() == null && chatMessage.getSenderId() != null) {
            Player sender = playerList.get(chatMessage.getSenderId());
            if (sender != null) {
                chatMessage.setSender(sender.getName());
            }
        }

        // Используем дефолтную комнату если roomId не указан
        String roomId = chatMessage.getRoomId() != null ? chatMessage.getRoomId() : DEFAULT_ROOM_ID;
        chatMessage.setRoomId(roomId);

        // Добавляем сообщение в историю комнаты
        addMessageToRoomHistory(roomId, chatMessage);

        log.debug("Message sent to room {}: {} from {}", roomId, chatMessage.getContent(), chatMessage.getSender());
    }

    /**
     * Добавляет сообщение в историю конкретной комнаты с проверкой лимита
     */
    private void addMessageToRoomHistory(String roomId, ChatMessage message) {
        ReadWriteLock lock = getLockForRoom(roomId);
        lock.writeLock().lock();

        try {
            List<ChatMessage> roomMessages = messageHistoryByRoom.computeIfAbsent(roomId, k -> new ArrayList<>());
            roomMessages.add(message);

            // Проверяем, не превышен ли лимит сообщений в памяти для этой комнаты
            if (roomMessages.size() >= MAX_MESSAGES_IN_MEMORY) {
                flushRoomMessages(roomId, roomMessages);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Получает или создает блокировку для комнаты
     */
    private ReadWriteLock getLockForRoom(String roomId) {
        return locksByRoom.computeIfAbsent(roomId, k -> new ReentrantReadWriteLock());
    }

    /**
     * Сохраняет сообщения и оставляет только последние KEEP_MESSAGES_IN_MEMORY в памяти
     */
    private void flushRoomMessages(String roomId, List<ChatMessage> messages) {
        try {
            if (useDatabaseStorage && chatMessageRepository != null) {
                flushRoomMessagesToDatabase(roomId, messages);
            }

            if (useFileBackup || !useDatabaseStorage) {
                flushRoomMessagesToFile(roomId, messages);
            }

            // Оставляем только последние KEEP_MESSAGES_IN_MEMORY сообщений в памяти
            if (messages.size() > KEEP_MESSAGES_IN_MEMORY) {
                List<ChatMessage> recentMessages = messages.subList(
                        messages.size() - KEEP_MESSAGES_IN_MEMORY,
                        messages.size()
                );
                messages.clear();
                messages.addAll(recentMessages);

                log.info("Flushed messages for room {} and kept {} recent messages in memory",
                        roomId, KEEP_MESSAGES_IN_MEMORY);
            }

        } catch (Exception e) {
            log.error("Failed to flush messages for room {}: {}", roomId, e.getMessage(), e);
        }
    }

    /**
     * Сохраняет сообщения комнаты в базу данных
     */
    @Transactional
    private void flushRoomMessagesToDatabase(String roomId, List<ChatMessage> messages) {
        try {
            // Сохраняем сообщения в БД
            List<ChatMessageModel> entities = messages.stream()
                    .map(chatMessageMapper::toChatMessageModel)
                    .collect(Collectors.toList());

            chatMessageRepository.saveAll(entities);

            // Удаляем старые сообщения, оставляя только последние KEEP_MESSAGES_IN_MEMORY в БД
            int deletedCount = chatMessageRepository.deleteOldMessagesKeepRecent(roomId, KEEP_MESSAGES_IN_MEMORY);

            log.info("Saved {} messages to database for room {} and deleted {} old messages",
                    entities.size(), roomId, deletedCount);

        } catch (Exception e) {
            log.error("Failed to save messages to database for room {}: {}", roomId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Сохраняет сообщения комнаты в файл
     */
    private void flushRoomMessagesToFile(String roomId, List<ChatMessage> messages) {
        try {
            Path logFilePath = getLogFilePathForRoom(roomId);
            createLogFileIfNotExists(logFilePath);

            List<String> lines = new ArrayList<>();

            for (ChatMessage message : messages) {
                String logLine = formatMessageForFile(message);
                lines.add(logLine);
            }

            Files.write(logFilePath, lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            log.info("Saved {} messages to file {} for room {}", lines.size(), logFilePath.getFileName(), roomId);

        } catch (IOException e) {
            log.error("Failed to write messages to log file for room {}: {}", roomId, e.getMessage(), e);
        }
    }

    /**
     * Получает путь к файлу лога для конкретной комнаты
     */
    private Path getLogFilePathForRoom(String roomId) {
        String sanitizedRoomId = sanitizeFileName(roomId);
        return Paths.get(sanitizedRoomId + "_chat_logs" + LOG_FILE_EXTENSION);
    }

    /**
     * Очищает строку от символов, недопустимых в имени файла
     */
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Форматирует сообщение для записи в файл
     */
    private String formatMessageForFile(ChatMessage message) {
        return String.format("[%s] [%s] [%s] %s (%d): %s",
                message.getTimestamp().format(TIMESTAMP_FORMATTER),
                message.getRoomId(),
                message.getType().name(),
                message.getSender() != null ? message.getSender() : "Unknown",
                message.getSenderId() != null ? message.getSenderId() : 0,
                message.getContent());
    }

    /**
     * Возвращает историю сообщений конкретной комнаты из памяти
     */
    public List<ChatMessage> getRecentMessages(String roomId) {
        String targetRoomId = roomId != null ? roomId : DEFAULT_ROOM_ID;
        ReadWriteLock lock = getLockForRoom(targetRoomId);
        lock.readLock().lock();

        try {
            List<ChatMessage> roomMessages = messageHistoryByRoom.get(targetRoomId);
            return roomMessages != null ? new ArrayList<>(roomMessages) : new ArrayList<>();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Возвращает историю сообщений комнаты из БД с возможностью пагинации
     */
    public List<ChatMessage> getMessagesFromDatabase(String roomId, int limit) {
        if (!useDatabaseStorage || chatMessageRepository == null) {
            return new ArrayList<>();
        }

        String targetRoomId = roomId != null ? roomId : DEFAULT_ROOM_ID;
        List<ChatMessageModel> entities = chatMessageRepository
                .findByRoomIdOrderByTimestampDesc(targetRoomId, PageRequest.of(0, limit));

        return entities.stream()
                .map(chatMessageMapper::toChatMessage)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает объединенную историю сообщений (память + БД)
     */
    public List<ChatMessage> getCombinedMessages(String roomId, int totalLimit) {
        List<ChatMessage> memoryMessages = getRecentMessages(roomId);

        if (!useDatabaseStorage || chatMessageRepository == null) {
            return memoryMessages.size() > totalLimit ?
                    memoryMessages.subList(Math.max(0, memoryMessages.size() - totalLimit), memoryMessages.size()) :
                    memoryMessages;
        }

        int remainingCount = Math.max(0, totalLimit - memoryMessages.size());
        if (remainingCount > 0) {
            List<ChatMessage> dbMessages = getMessagesFromDatabase(roomId, remainingCount);

            // Объединяем сообщения, избегая дубликатов
            List<ChatMessage> combined = new ArrayList<>(dbMessages);
            combined.addAll(memoryMessages);

            // Сортируем по времени и ограничиваем количество
            return combined.stream()
                    .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                    .limit(totalLimit)
                    .collect(Collectors.toList());
        }

        return memoryMessages;
    }

    /**
     * Возвращает историю сообщений всех комнат из памяти
     */
    public Map<String, List<ChatMessage>> getAllRecentMessages() {
        Map<String, List<ChatMessage>> result = new HashMap<>();

        for (String roomId : messageHistoryByRoom.keySet()) {
            result.put(roomId, getRecentMessages(roomId));
        }

        return result;
    }

    /**
     * Возвращает количество сообщений в памяти для конкретной комнаты
     */
    public int getMessagesInMemoryCount(String roomId) {
        String targetRoomId = roomId != null ? roomId : DEFAULT_ROOM_ID;
        ReadWriteLock lock = getLockForRoom(targetRoomId);
        lock.readLock().lock();

        try {
            List<ChatMessage> roomMessages = messageHistoryByRoom.get(targetRoomId);
            return roomMessages != null ? roomMessages.size() : 0;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Возвращает общее количество сообщений в памяти по всем комнатам
     */
    public int getTotalMessagesInMemoryCount() {
        return messageHistoryByRoom.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    /**
     * Возвращает список всех активных комнат
     */
    public List<String> getActiveRooms() {
        List<String> memoryRooms = new ArrayList<>(messageHistoryByRoom.keySet());

        if (useDatabaseStorage && chatMessageRepository != null) {
            List<String> dbRooms = chatMessageRepository.findAllRoomIds();
            memoryRooms.addAll(dbRooms);
            return memoryRooms.stream().distinct().collect(Collectors.toList());
        }

        return memoryRooms;
    }

    /**
     * Принудительно сохраняет все сообщения конкретной комнаты
     */
    public void forceFlushRoomToFile(String roomId) {
        String targetRoomId = roomId != null ? roomId : DEFAULT_ROOM_ID;
        ReadWriteLock lock = getLockForRoom(targetRoomId);
        lock.writeLock().lock();

        try {
            List<ChatMessage> roomMessages = messageHistoryByRoom.get(targetRoomId);
            if (roomMessages != null && !roomMessages.isEmpty()) {
                if (useDatabaseStorage && chatMessageRepository != null) {
                    flushRoomMessagesToDatabase(targetRoomId, new ArrayList<>(roomMessages));
                }

                if (useFileBackup || !useDatabaseStorage) {
                    flushRoomMessagesToFile(targetRoomId, roomMessages);
                }

                log.info("Force flushed all messages for room {} to storage", targetRoomId);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Принудительно сохраняет все сообщения всех комнат
     */
    public void forceFlushAllToFile() {
        for (String roomId : messageHistoryByRoom.keySet()) {
            forceFlushRoomToFile(roomId);
        }
        log.info("Force flushed all messages from all rooms to storage");
    }

    /**
     * Создает файл лога если он не существует
     */
    private void createLogFileIfNotExists(Path logFilePath) {
        try {
            if (!Files.exists(logFilePath)) {
                Files.createFile(logFilePath);
                log.info("Created new log file: {}", logFilePath.getFileName());
            }
        } catch (IOException e) {
            log.error("Failed to create log file {}: {}", logFilePath.getFileName(), e.getMessage(), e);
        }
    }

    /**
     * Очищает всю историю конкретной комнаты (в памяти и в хранилище)
     */
    @Transactional
    public void clearRoomHistory(String roomId) {
        String targetRoomId = roomId != null ? roomId : DEFAULT_ROOM_ID;
        ReadWriteLock lock = getLockForRoom(targetRoomId);
        lock.writeLock().lock();

        try {
            // Очищаем память
            List<ChatMessage> roomMessages = messageHistoryByRoom.get(targetRoomId);
            if (roomMessages != null) {
                roomMessages.clear();
            }

            // Очищаем БД
            if (useDatabaseStorage && chatMessageRepository != null) {
                chatMessageRepository.deleteByRoomId(targetRoomId);
            }

            // Удаляем файл лога
            if (useFileBackup || !useDatabaseStorage) {
                try {
                    Path logFilePath = getLogFilePathForRoom(targetRoomId);
                    Files.deleteIfExists(logFilePath);
                    createLogFileIfNotExists(logFilePath);
                } catch (IOException e) {
                    log.error("Failed to clear log file for room {}: {}", targetRoomId, e.getMessage(), e);
                }
            }

            log.info("Cleared chat history for room: {}", targetRoomId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Очищает всю историю всех комнат
     */
    public void clearAllHistory() {
        for (String roomId : new ArrayList<>(getActiveRooms())) {
            clearRoomHistory(roomId);
        }
        log.info("Cleared all chat history for all rooms");
    }
}