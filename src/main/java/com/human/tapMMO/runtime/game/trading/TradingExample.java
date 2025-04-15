package com.human.tapMMO.runtime.game.trading;

import com.human.tapMMO.service.game.AuctionService;
import com.human.tapMMO.service.game.TradeService;

import java.math.BigDecimal;
import java.util.List;

class TradingExample {

    public void demoAuctionUsage(AuctionService auctionService) {
        // Создание аукциона
        String sellerId = "player123";
        String itemId = "rare_sword";
        int quantity = 1;
        BigDecimal startingPrice = new BigDecimal("100.00");
        BigDecimal buyoutPrice = new BigDecimal("500.00");
        int durationHours = 24;

        Auction auction = auctionService.createAuction(
                sellerId, itemId, quantity, startingPrice, buyoutPrice, durationHours);

        System.out.println("Created auction: " + auction.getId());

        // Размещение ставки
        String bidderId = "player456";
        BigDecimal bidAmount = new BigDecimal("150.00");

        boolean bidPlaced = auctionService.placeBid(auction.getId(), bidderId, bidAmount);

        if (bidPlaced) {
            System.out.println("Bid successfully placed");
        }

        // Поиск аукционов
        List<Auction> searchResults = auctionService.searchAuctions(
                "rare_sword", new BigDecimal("100.00"), new BigDecimal("1000.00"), null, false);

        System.out.println("Found " + searchResults.size() + " auctions matching criteria");

        // Выкуп предмета
        String buyerId = "player789";
        boolean buyout = auctionService.buyoutAuction(auction.getId(), buyerId);

        if (buyout) {
            System.out.println("Item successfully bought out");
        }
    }

    public void demoTradeUsage(TradeService tradeService) {
        // Инициирование обмена
        String initiatorId = "player123";
        String targetId = "player456";

        Trade trade = tradeService.requestTrade(initiatorId, targetId);
        System.out.println("Trade requested: " + trade.getId());

        // Добавление предметов инициатором
        TradeItem item1 = new TradeItem("health_potion", "item_instance_001", 5);
        item1.addProperty("quality", "normal");

        boolean added = tradeService.addItemToTrade(trade.getId(), initiatorId, item1);
        if (added) {
            System.out.println("Item added to trade");
        }

        // Добавление золота целевым игроком
        boolean goldSet = tradeService.setGoldInTrade(trade.getId(), targetId, new BigDecimal("50.00"));
        if (goldSet) {
            System.out.println("Gold set in trade");
        }

        // Подтверждение обмена обоими игроками
        boolean initiatorAccepted = tradeService.acceptTrade(trade.getId(), initiatorId);
        boolean targetAccepted = tradeService.acceptTrade(trade.getId(), targetId);

        if (initiatorAccepted && targetAccepted) {
            System.out.println("Trade completed successfully");
        }
    }
}
