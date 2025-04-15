package com.human.tapMMO.controller;

import com.human.tapMMO.model.tables.Group;
import com.human.tapMMO.runtime.game.actor.player.Player;
import com.human.tapMMO.service.game.GroupService;
import com.human.tapMMO.service.game.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<ApiResponse<Group>> createGroup(@RequestBody GroupCreateRequest request) {
        Player currentPlayer = playerService.getPlayerById(SecurityUtil.getCurrentPlayerId());
        Group group = groupService.createGroup(request.getName(), currentPlayer, request.getGroupType());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Group created successfully", group));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Group>> getGroup(@PathVariable Long id) {
        Group group = groupService.getGroupById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Group retrieved successfully", group));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> disbandGroup(@PathVariable Long id) {
        Player currentPlayer = playerService.getPlayerById(SecurityUtil.getCurrentPlayerId());
        groupService.disbandGroup(id, currentPlayer);

        return ResponseEntity.ok(new ApiResponse<>(true, "Group disbanded successfully", null));
    }

    @PostMapping("/{id}/members/{playerId}")
    public ResponseEntity<ApiResponse<Void>> addMember(
            @PathVariable Long id,
            @PathVariable Long playerId) {
        Player currentPlayer = playerService.getPlayerById(SecurityUtil.getCurrentPlayerId());
        groupService.addMember(id, playerId, currentPlayer);

        return ResponseEntity.ok(new ApiResponse<>(true, "Member added successfully", null));
    }

    @DeleteMapping("/{id}/members/{playerId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable Long id,
            @PathVariable Long playerId) {
        Player currentPlayer = playerService.getPlayerById(SecurityUtil.getCurrentPlayerId());
        groupService.removeMember(id, playerId, currentPlayer);

        return ResponseEntity.ok(new ApiResponse<>(true, "Member removed successfully", null));
    }

    @PutMapping("/{id}/leader/{playerId}")
    public ResponseEntity<ApiResponse<Void>> transferLeadership(
            @PathVariable Long id,
            @PathVariable Long playerId) {
        Player currentPlayer = playerService.getPlayerById(SecurityUtil.getCurrentPlayerId());
        groupService.transferLeadership(id, playerId, currentPlayer);

        return ResponseEntity.ok(new ApiResponse<>(true, "Leadership transferred successfully", null));
    }

    @GetMapping("/player")
    public ResponseEntity<ApiResponse<List<Group>>> getPlayerGroups() {
        Long currentPlayerId = SecurityUtil.getCurrentPlayerId();
        List<Group> groups = groupService.getPlayerGroups(currentPlayerId);

        return ResponseEntity.ok(new ApiResponse<>(true, "Player groups retrieved successfully", groups));
    }

    @GetMapping("/looking-for-more")
    public ResponseEntity<ApiResponse<List<Group>>> getGroupsLookingForMore(
            @RequestParam(required = false) GroupType groupType) {
        List<Group> groups = groupService.findGroupsLookingForMore(groupType);

        return ResponseEntity.ok(new ApiResponse<>(true, "Groups looking for more retrieved successfully", groups));
    }

    @PutMapping("/{id}/looking-for-more")
    public ResponseEntity<ApiResponse<Void>> setLookingForMore(
            @PathVariable Long id,
            @RequestParam Boolean isLookingForMore) {
        Player currentPlayer = playerService.getPlayerById(SecurityUtil.getCurrentPlayerId());
        groupService.setGroupLookingForMore(id, isLookingForMore, currentPlayer);

        return ResponseEntity.ok(new ApiResponse<>(true, "Group settings updated successfully", null));
    }
}
