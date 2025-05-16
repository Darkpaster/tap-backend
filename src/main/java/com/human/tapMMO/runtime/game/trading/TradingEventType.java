package com.human.tapMMO.runtime.game.trading;

// Типы событий для обмена и аукциона
public enum TradingEventType {
    AUCTION_CREATED, AUCTION_BID_PLACED, AUCTION_ENDED, AUCTION_CANCELLED,
    TRADE_REQUESTED, TRADE_ITEM_ADDED, TRADE_ITEM_REMOVED, TRADE_ACCEPTED, TRADE_CANCELLED
}
