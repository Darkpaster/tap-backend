package com.human.tapMMO.runtime.game.trading;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TradingEvent {
    // Геттеры
    private String eventId;
    private TradingEventType type;
    private String playerId;
    private String targetPlayerId;
    private String itemId;
    private int amount;
    private LocalDateTime timestamp;

    public TradingEvent(String eventId, TradingEventType type, String playerId, String targetPlayerId,
                        String itemId, int amount) {
        this.eventId = eventId;
        this.type = type;
        this.playerId = playerId;
        this.targetPlayerId = targetPlayerId;
        this.itemId = itemId;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

}

// Типы событий для обмена и аукциона
public enum TradingEventType {
    AUCTION_CREATED, AUCTION_BID_PLACED, AUCTION_ENDED, AUCTION_CANCELLED,
    TRADE_REQUESTED, TRADE_ITEM_ADDED, TRADE_ITEM_REMOVED, TRADE_ACCEPTED, TRADE_CANCELLED
}

