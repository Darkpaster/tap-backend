package com.human.tapMMO.controller;

import com.human.tapMMO.dto.rest.ProgressUpdateRequest;
import com.human.tapMMO.model.tables.Achievement;
import com.human.tapMMO.service.game.player.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/achievements")
//@RequiredArgsConstructor
public class AchievementController {

//    private final AchievementService achievementService;

//    @GetMapping
//    public ResponseEntity<List<Achievement>> getAllAchievements() {
//        return ResponseEntity.ok(achievementService.getAllAchievements());
//    }

//    @GetMapping("/player/{playerId}")
//    public ResponseEntity<List<PlayerAchievement>> getPlayerAchievements(@PathVariable Long playerId) {
//        return ResponseEntity.ok(achievementService.getPlayerAchievements(playerId));
//    }

//    @PostMapping("/progress")
//    public ResponseEntity<?> updateProgress(@RequestBody ProgressUpdateRequest request) {
//        achievementService.updateProgress(
//                request.getPlayerId(),
//                request.getObjectiveType(),
//                request.getObjectiveId(),
//                request.getIncrement()
//        );
//        return ResponseEntity.ok().build();
//    }

    // Admin endpoints for managing achievements would go here
}

