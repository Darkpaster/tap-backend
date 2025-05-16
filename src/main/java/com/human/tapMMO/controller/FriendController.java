package com.human.tapMMO.controller;

import com.human.tapMMO.service.game.social.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@RestController
//@RequestMapping("/friends")
//@RequiredArgsConstructor
public class FriendController {
//    private final FriendService friendService;
//
//    @GetMapping
//    public ResponseEntity<List<UserDto>> getFriendsList(@RequestParam Long userId) {
//        List<UserDto> friends = friendService.getUserFriends(userId);
//        return ResponseEntity.ok(friends);
//    }
//
//    @GetMapping("/requests/incoming")
//    public ResponseEntity<List<FriendRequestDto>> getIncomingRequests(@RequestParam Long userId) {
//        List<FriendRequestDto> requests = friendService.getIncomingFriendRequests(userId);
//        return ResponseEntity.ok(requests);
//    }
//
//    @GetMapping("/requests/outgoing")
//    public ResponseEntity<List<FriendRequestDto>> getOutgoingRequests(@RequestParam Long userId) {
//        List<FriendRequestDto> requests = friendService.getOutgoingFriendRequests(userId);
//        return ResponseEntity.ok(requests);
//    }
//
//    @PostMapping("/requests")
//    public ResponseEntity<FriendRequestDto> sendFriendRequest(
//            @RequestParam Long senderId,
//            @RequestBody FriendRequestCreateDto requestDto) {
//        FriendRequestDto createdRequest = friendService.sendFriendRequest(senderId, requestDto.getReceiverId());
//        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
//    }
//
//    @PutMapping("/requests/{requestId}/accept")
//    public ResponseEntity<Void> acceptFriendRequest(
//            @RequestParam Long userId,
//            @PathVariable Long requestId) {
//        friendService.acceptFriendRequest(userId, requestId);
//        return ResponseEntity.ok().build();
//    }
//
//    @PutMapping("/requests/{requestId}/reject")
//    public ResponseEntity<Void> rejectFriendRequest(
//            @RequestParam Long userId,
//            @PathVariable Long requestId) {
//        friendService.rejectFriendRequest(userId, requestId);
//        return ResponseEntity.ok().build();
//    }
//
//    @DeleteMapping("/{friendId}")
//    public ResponseEntity<Void> removeFriend(
//            @RequestParam Long userId,
//            @PathVariable Long friendId) {
//        friendService.removeFriend(userId, friendId);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String query) {
//        List<UserDto> users = friendService.searchUsers(query);
//        return ResponseEntity.ok(users);
//    }
}
