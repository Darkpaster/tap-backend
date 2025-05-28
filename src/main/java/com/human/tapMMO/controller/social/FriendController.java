package com.human.tapMMO.controller.social;

import com.human.tapMMO.service.game.social.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/request/{senderId}/{receiverId}")
    public ResponseEntity<FriendRequestDto> sendFriendRequest(@PathVariable Long senderId, @PathVariable Long receiverId) {
        try {
            FriendRequestDto request = friendService.sendFriendRequest(senderId, receiverId);
            return ResponseEntity.ok(request);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/accept/{userId}/{requestId}")
    public ResponseEntity<Void> acceptFriendRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        try {
            friendService.acceptFriendRequest(userId, requestId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/reject/{userId}/{requestId}")
    public ResponseEntity<Void> rejectFriendRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        try {
            friendService.rejectFriendRequest(userId, requestId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{userId}/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        try {
            friendService.removeFriend(userId, friendId);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserDto>> getUserFriends(@PathVariable Long userId) {
        try {
            List<UserDto> friends = friendService.getUserFriends(userId);
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/requests/incoming/{userId}")
    public ResponseEntity<List<FriendRequestDto>> getIncomingFriendRequests(@PathVariable Long userId) {
        try {
            List<FriendRequestDto> requests = friendService.getIncomingFriendRequests(userId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/requests/outgoing/{userId}")
    public ResponseEntity<List<FriendRequestDto>> getOutgoingFriendRequests(@PathVariable Long userId) {
        try {
            List<FriendRequestDto> requests = friendService.getOutgoingFriendRequests(userId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String query) {
        List<UserDto> users = friendService.searchUsers(query);
        return ResponseEntity.ok(users);
    }
}
