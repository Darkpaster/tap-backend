package com.human.tapMMO.service.game.player;

import com.human.tapMMO.model.tables.Achievement;
import com.human.tapMMO.repository.AchievementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

//@Service
//@RequiredArgsConstructor
public class AchievementService {

//    private final AchievementRepository achievementRepository;
//    private final AchievementRepository playerAchievementRepository;
//    private final PlayerService playerService;
//
//    public List<Achievement> getAllAchievements() {
//        return achievementRepository.findAll();
//    }
//
//    public List<Achievement> getPlayerAchievements(Long playerId) {
//        return playerAchievementRepository.findByPlayerId(playerId);
//    }
//
//    @Transactional
//    public void initializePlayerAchievements(Player player) {
//        List<Achievement> achievements = achievementRepository.findAll();
//
//        for (Achievement achievement : achievements) {
//            if (!playerAchievementRepository.existsByPlayerAndAchievement(player, achievement)) {
//                Achievement playerAchievement = new Achievement();
//                playerAchievement.setPlayer(player);
//                playerAchievement.setAchievement(achievement);
//                playerAchievement.setCompleted(false);
//
//                // Initialize progress for each criteria
//                Map<String, Integer> progress = playerAchievement.getCriteriaProgress();
//                achievement.getCriteria().forEach(criteria ->
//                        progress.put(criteria.getObjectiveId(), 0)
//                );
//
//                playerAchievementRepository.save(playerAchievement);
//            }
//        }
//    }
//
//    @Transactional
//    public void updateProgress(Long playerId, String objectiveType, String objectiveId, int increment) {
//        Player player = playerService.getPlayerById(playerId);
//        List<PlayerAchievement> playerAchievements = playerAchievementRepository.findByPlayerAndNotCompleted(player);
//
//        for (PlayerAchievement playerAchievement : playerAchievements) {
//            Achievement achievement = playerAchievement.getAchievement();
//            boolean updated = false;
//
//            for (AchievementCriteria criteria : achievement.getCriteria()) {
//                if (criteria.getCriteriaType().equals(objectiveType) && criteria.getObjectiveId().equals(objectiveId)) {
//                    Map<String, Integer> progress = playerAchievement.getCriteriaProgress();
//                    int currentProgress = progress.getOrDefault(objectiveId, 0);
//                    int newProgress = Math.min(currentProgress + increment, criteria.getRequiredCount());
//                    progress.put(objectiveId, newProgress);
//                    updated = true;
//                }
//            }
//
//            if (updated) {
//                checkAchievementCompletion(playerAchievement);
//                playerAchievementRepository.save(playerAchievement);
//            }
//        }
//    }
//
//    private void checkAchievementCompletion(PlayerAchievement playerAchievement) {
//        if (playerAchievement.isCompleted()) {
//            return;
//        }
//
//        Achievement achievement = playerAchievement.getAchievement();
//        Map<String, Integer> progress = playerAchievement.getCriteriaProgress();
//
//        boolean allCompleted = achievement.getCriteria().stream().allMatch(criteria -> {
//            int currentProgress = progress.getOrDefault(criteria.getObjectiveId(), 0);
//            return currentProgress >= criteria.getRequiredCount();
//        });
//
//        if (allCompleted) {
//            playerAchievement.setCompleted(true);
//            playerAchievement.setCompletedAt(LocalDateTime.now());
//
//            // Give rewards
//            Player player = playerAchievement.getPlayer();
//            player.addExperience(achievement.getExperienceReward());
//            player.addCurrency(achievement.getCurrencyReward());
//
//            // Additional logic to give item rewards would go here
//
//            playerService.updatePlayer(player);
//        }
//    }
}
