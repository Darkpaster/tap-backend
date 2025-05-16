package com.human.tapMMO.controller;

import com.human.tapMMO.model.tables.Guild;
import com.human.tapMMO.runtime.game.actors.player.Player;
import com.human.tapMMO.service.game.social.GuildService;
import com.human.tapMMO.service.game.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/guild")
//@RequiredArgsConstructor
public class GuildController {
//    private final GuildService guildService;
//    private final PlayerService playerService;
//
//    @PostMapping
//    public ResponseEntity<ApiResponse<Guild>> createGuild(@RequestBody GuildCreateRequest request) {
//        Player currentPlayer = playerService.getPlayerById(SecurityUtil.getCurrentPlayerId());
//        Guild guild = guildService.createGuild(request.getName(), request.getDescription(), currentPlayer);
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(new ApiResponse<>(true, "Guild created successfully", guild));
//    }
//
//    @GetMapping
//    public ResponseEntity<ApiResponse<List<Guild>>> searchGuilds(@RequestParam(required = false) String name) {
//        List<Guild> guilds;
//        if (name != null && !name.isBlank()) {
//            guilds = guildService.searchGuilds(name);
//        } else {
//            guilds = guildService.searchGuilds("");
//        }
//
//        return ResponseEntity.ok(new ApiResponse<>(true, "Guilds retrieved successfully", guilds));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse<Guild>> getGuild(@PathVariable Long id) {
//        Guild guild = guildService.getGuildById(id);
//        return ResponseEntity.ok(new ApiResponse<>(true, "Guild retrieved successfully", guild));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse<Guild>> updateGuild(
//            @PathVariable Long id,
//            @RequestBody GuildUpdateRequest request) {
//        Player currentPlayer = playerService.getPlayerById(SecurityUtil.getCurrentPlayerId());
//        Guild guildDetails = new Guild();
//        guildDetails.setDescription(request.getDescription());
//        guildDetails.setMessageOfTheDay(request.getMessageOfTheDay());
//        guildDetails.setGuildCrestUrl(request.getGuildCrestUrl());
//
//        Guild updatedGuild = guildService.updateGuild(id, guildDetails, currentPlayer);
//
//        return ResponseEntity.ok(new ApiResponse<>(true, "Guild updated successfully", updatedGuild));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<ApiResponse<Void>> disbandGuild(@PathVariable Long id) {
//        Player currentPlayer = playerService.getPlayerById(SecurityUtil.getCurrentPlayerId());
//        guildService.disbandGuild(id, currentPlayer);
//
//        return ResponseEntity.ok(new ApiResponse<>(true, "Guild disbanded successfully", null));
//    }
//
//    @GetMapping("/{id}/members")
//    public ResponseEntity<ApiResponse<List<Player>>> getGuildMembers(@PathVariable Long id) {
//        List<Player> members = guildService.getGuildMembers(id);
//        return ResponseEntity.ok(new ApiResponse<>(true, "Guild members retrieved successfully", members));
//    }
//
//    @PostMapping("/{id}/members/{playerId}")
//    public ResponseEntity<ApiResponse<Void>> addMember(
//            @PathVariable Long id,
//            @PathVariable Long playerId) {
//        Player currentPlayer = playerService.getPlayerById(SecurityUtil.getCurrentPlayerId());
//        guildService.addMember(id, playerId, currentPlayer);
//
//        return ResponseEntity.ok(new ApiResponse<>(true, "Member added successfully", null));
//    }
//
//    @DeleteMapping("/{id}/members/{playerId}")
//    public ResponseEntity<ApiResponse<Void>> removeMember(
//            @PathVariable Long id,
//            @PathVariable Long playerId) {
//        Player currentPlayer = playerService.getPlayerById(SecurityUtil.getCurrentPlayerId());
//        guildService.removeMember(id, playerId, currentPlayer);
//
//        return ResponseEntity.ok(new ApiResponse<>(true, "Member removed successfully", null));
//    }
//
//    @PutMapping("/{id}/members/{playerId}/rank")
//    public ResponseEntity<ApiResponse<Void>> changeRank(
//            @PathVariable Long id,
//            @PathVariable Long playerId,
//            @RequestParam GuildRank rank) {
//        Player currentPlayer = playerService.getPlayerById(SecurityUtil.getCurrentPlayerId());
//        guildService.changeRank(id, playerId, rank, currentPlayer);
//
//        return ResponseEntity.ok(new ApiResponse<>(true, "Member rank changed successfully", null));
//    }
//
//    @PostMapping("/{id}/experience")
//    public ResponseEntity<ApiResponse<Void>> addExperience(
//            @PathVariable Long id,
//            @RequestParam int amount) {
//        guildService.addGuildExperience(id, amount);
//
//        return ResponseEntity.ok(new ApiResponse<>(true, "Guild experience added successfully", null));
//    }
//
//    @PutMapping("/{id}/bank")
//    public ResponseEntity<ApiResponse<Void>> updateBank(
//            @PathVariable Long id,
//            @RequestParam Long itemId,
//            @RequestParam int quantity,
//            @RequestParam int tabId,
//            @RequestParam int slotId) {
//        Player currentPlayer = playerService.getPlayerById(SecurityUtil.getCurrentPlayerId());
//        guildService.updateGuildBank(id, itemId, quantity, tabId, slotId, currentPlayer);
//
//        return ResponseEntity.ok(new ApiResponse<>(true, "Guild bank updated successfully", null));
//    }
}
