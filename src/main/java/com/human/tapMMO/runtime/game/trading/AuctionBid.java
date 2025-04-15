package com.human.tapMMO.runtime.game.trading;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class AuctionBid {
    // Геттеры
    private String id;
    private String auctionId;
    private String bidderId;
    private BigDecimal bidAmount;
    private LocalDateTime bidTime;

    public AuctionBid(String id, String auctionId, String bidderId, BigDecimal bidAmount) {
        this.id = id;
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.bidAmount = bidAmount;
        this.bidTime = LocalDateTime.now();
    }

}
