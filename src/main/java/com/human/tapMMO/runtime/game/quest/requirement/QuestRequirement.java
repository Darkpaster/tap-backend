package com.human.tapMMO.runtime.game.quest.requirement;

import com.human.tapMMO.runtime.game.quest.PlayerContext;

public interface QuestRequirement {
    boolean isMet(PlayerContext playerContext);
    String getDescription();
}
