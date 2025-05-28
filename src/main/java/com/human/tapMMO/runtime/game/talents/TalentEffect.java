package com.human.tapMMO.runtime.game.talents;

import com.human.tapMMO.runtime.game.actors.player.Player;

public interface TalentEffect {
    void onActivate(Player player);
    void onUpgrade(Player player, int newTier);
}
