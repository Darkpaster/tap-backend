package com.human.tapMMO.runtime.game.quests.requirement;

import com.human.tapMMO.runtime.game.quests.PlayerContext;

public class LevelRequirement implements QuestRequirement {
    private int requiredLevel;

    public LevelRequirement(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    @Override
    public boolean isMet(PlayerContext playerContext) {
        return playerContext.getStat("level") >= requiredLevel;
    }

    @Override
    public String getDescription() {
        return "Требуемый уровень: " + requiredLevel;
    }
}
