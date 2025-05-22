package com.human.tapMMO.runtime.game.actors.mob;

import com.human.tapMMO.runtime.game.actors.player.Player;

import java.util.function.Function;

public class MobServiceList {
    public final Function<Player, Player> sendDamage;
    public final Function<Player, Player> sendHealing;
    public final Function<Player, Player> sendBuff;
    public final Function<Player, Player> sendNewQuest;
    public final Function<Player, Player> sendUpdatedQuest;
    public final Function<Player, Player> sendCompletedQuest;

    public MobServiceList(Function<Player, Player> sendDamage, Function<Player, Player> sendHealing, Function<Player, Player> sendBuff, Function<Player, Player> sendNewQuest, Function<Player, Player> sendUpdatedQuest, Function<Player, Player> sendCompletedQuest) {
        this.sendDamage = sendDamage;
        this.sendHealing = sendHealing;
        this.sendBuff = sendBuff;
        this.sendNewQuest = sendNewQuest;
        this.sendUpdatedQuest = sendUpdatedQuest;
        this.sendCompletedQuest = sendCompletedQuest;
    }
}
