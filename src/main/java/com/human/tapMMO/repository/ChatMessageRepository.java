package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.ChatMessageModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageModel, Long> {

    /**
     * Находит последние сообщения для конкретной комнаты
     */
    List<ChatMessageModel> findByRoomIdOrderByTimestampDesc(String roomId, Pageable pageable);

    /**
     * Находит сообщения в диапазоне времени для комнаты
     */
    List<ChatMessageModel> findByRoomIdAndTimestampBetweenOrderByTimestamp(
            String roomId, LocalDateTime start, LocalDateTime end);

    /**
     * Находит сообщения от конкретного пользователя в комнате
     */
    List<ChatMessageModel> findByRoomIdAndSenderIdOrderByTimestampDesc(
            String roomId, Long senderId, Pageable pageable);

    /**
     * Подсчитывает количество сообщений в комнате
     */
    long countByRoomId(String roomId);

    /**
     * Находит все уникальные roomId
     */
    @Query("SELECT DISTINCT c.roomId FROM ChatMessageModel c")
    List<String> findAllRoomIds();

    /**
     * Удаляет старые сообщения для комнаты, оставляя только указанное количество последних
     */
    @Modifying
    @Query(value = """
            DELETE FROM chat_messages 
            WHERE id NOT IN (
                SELECT id FROM (
                    SELECT id FROM chat_messages 
                    WHERE room_id = :roomId 
                    ORDER BY timestamp DESC 
                    LIMIT :keepCount
                ) AS recent_messages
            ) AND room_id = :roomId
            """, nativeQuery = true)
    int deleteOldMessagesKeepRecent(@Param("roomId") String roomId, @Param("keepCount") int keepCount);

    /**
     * Удаляет все сообщения комнаты
     */
    void deleteByRoomId(String roomId);

    /**
     * Находит последние сообщения по всем комнатам
     */
    @Query("SELECT c FROM ChatMessageModel c WHERE c.timestamp >= :since ORDER BY c.timestamp DESC")
    List<ChatMessageModel> findRecentMessagesAcrossAllRooms(@Param("since") LocalDateTime since, Pageable pageable);
}