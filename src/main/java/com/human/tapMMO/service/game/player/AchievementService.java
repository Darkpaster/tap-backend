package com.human.tapMMO.service.game.player;

import com.human.tapMMO.model.tables.Achievement;
import com.human.tapMMO.repository.AchievementRepository;
import com.human.tapMMO.runtime.game.achievements.AchievementType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final PlayerService playerService;

    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    public List<Achievement> getPlayerAchievements(Long accountId) {
        return achievementRepository.findByAccountId(accountId);
    }

    @Transactional
    public Achievement createAchievement(Long accountId, String name, AchievementType type) {
        Achievement achievement = new Achievement();
        achievement.setAccountId(accountId);
        achievement.setName(name);
        achievement.setType(type);
        return achievementRepository.save(achievement);
    }

    public Optional<Achievement> getAchievementById(Long id) {
        return achievementRepository.findById(id);
    }

    @Transactional
    public void deleteAchievement(Long achievementId) {
        achievementRepository.deleteById(achievementId);
    }

    public boolean hasAchievement(Long accountId, String achievementName) {
        return achievementRepository.findByAccountIdAndName(accountId, achievementName).isPresent();
    }
}
