package com.human.tapMMO.controller.players;

import com.human.tapMMO.model.tables.Achievement;
import com.human.tapMMO.runtime.game.achievements.AchievementType;
import com.human.tapMMO.service.game.player.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping
    public ResponseEntity<List<Achievement>> getAllAchievements() {
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Achievement>> getPlayerAchievements(@PathVariable Long accountId) {
        return ResponseEntity.ok(achievementService.getPlayerAchievements(accountId));
    }

    @PostMapping("/account/{accountId}/create")
    public ResponseEntity<Achievement> createAchievement(@PathVariable Long accountId,
                                                         @RequestParam String name,
                                                         @RequestParam AchievementType type) {
        Achievement achievement = achievementService.createAchievement(accountId, name, type);
        return ResponseEntity.ok(achievement);
    }

    @GetMapping("/{achievementId}")
    public ResponseEntity<Achievement> getAchievementById(@PathVariable Long achievementId) {
        Optional<Achievement> achievement = achievementService.getAchievementById(achievementId);
        return achievement.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{achievementId}")
    public ResponseEntity<?> deleteAchievement(@PathVariable Long achievementId) {
        achievementService.deleteAchievement(achievementId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/account/{accountId}/has/{achievementName}")
    public ResponseEntity<Boolean> hasAchievement(@PathVariable Long accountId, @PathVariable String achievementName) {
        boolean hasAchievement = achievementService.hasAchievement(accountId, achievementName);
        return ResponseEntity.ok(hasAchievement);
    }
}