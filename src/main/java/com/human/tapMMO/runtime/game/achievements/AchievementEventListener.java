package com.human.tapMMO.runtime.game.achievements;

import com.human.tapMMO.service.game.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AchievementEventListener {

    private final AchievementService achievementService;

    @EventListener
    public void onMonsterKill(MonsterKilledEvent event) {
        achievementService.updateProgress(
                event.getPlayerId(),
                "KILL_MONSTER",
                event.getMonsterId().toString(),
                1
        );
    }

    @EventListener
    public void onQuestComplete(QuestCompletedEvent event) {
        achievementService.updateProgress(
                event.getPlayerId(),
                "COMPLETE_QUEST",
                event.getQuestId().toString(),
                1
        );
    }

    @EventListener
    public void onLocationDiscovered(LocationDiscoveredEvent event) {
        achievementService.updateProgress(
                event.getPlayerId(),
                "DISCOVER_LOCATION",
                event.getLocationId().toString(),
                1
        );
    }

    // Другие обработчики событий для различных действий игрока
}
