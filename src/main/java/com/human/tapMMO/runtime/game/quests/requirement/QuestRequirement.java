package com.human.tapMMO.runtime.game.quests.requirement;

import com.human.tapMMO.runtime.game.quests.PlayerContext;

public interface QuestRequirement {
    boolean isMet(PlayerContext playerContext);
    String getDescription();
}
