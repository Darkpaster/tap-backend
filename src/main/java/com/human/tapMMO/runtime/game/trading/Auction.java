package com.human.tapMMO.runtime.game.trading;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Auction {
    // Геттеры и сеттеры
    private String id;
    private String sellerId;
    private String itemId;
    private int quantity;
    private BigDecimal startingPrice;
    private BigDecimal buyoutPrice;
    @Setter
    private BigDecimal currentBid;
    @Setter
    private String currentBidderId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Setter
    private AuctionStatus status;
    private List<AuctionBid> bidHistory;

    public Auction(String id, String sellerId, String itemId, int quantity,
                   BigDecimal startingPrice, BigDecimal buyoutPrice,
                   LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.sellerId = sellerId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.startingPrice = startingPrice;
        this.buyoutPrice = buyoutPrice;
        this.currentBid = startingPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AuctionStatus.ACTIVE;
        this.bidHistory = new ArrayList<>();
    }

    public void addBid(AuctionBid bid) { this.bidHistory.add(bid); }

    // Проверка, завершен ли аукцион
    public boolean isEnded() {
        return LocalDateTime.now().isAfter(endTime) || status == AuctionStatus.COMPLETED || status == AuctionStatus.CANCELLED;
    }
}

