package com.human.tapMMO.runtime.game.quests.requirement;

import com.human.tapMMO.runtime.game.quests.PlayerContext;

class QuestCompletionRequirement implements QuestRequirement {
    private String questId;

    public QuestCompletionRequirement(String questId) {
        this.questId = questId;
    }

    @Override
    public boolean isMet(PlayerContext playerContext) {
        return playerContext.hasCompletedQuest(questId);
    }

    @Override
    public String getDescription() {
        return "Требуется выполнить квест: " + questId;
    }
}
