package com.human.tapMMO.repository;

import com.human.tapMMO.runtime.game.trading.Auction;
import com.human.tapMMO.runtime.game.trading.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

//public interface AuctionRepository extends JpaRepository<Auction, String> {
//    List<Auction> findByStatus(AuctionStatus status);
//    List<Auction> findBySellerIdAndStatus(String sellerId, AuctionStatus status);
//    List<Auction> findByCurrentBidderIdAndStatus(String bidderId, AuctionStatus status);
//    List<Auction> findByEndTimeBefore(LocalDateTime endTime);
//}
