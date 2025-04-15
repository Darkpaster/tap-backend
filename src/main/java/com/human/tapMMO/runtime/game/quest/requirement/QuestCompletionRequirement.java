package com.human.tapMMO.runtime.game.quest.requirement;

import com.human.tapMMO.runtime.game.quest.PlayerContext;

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
