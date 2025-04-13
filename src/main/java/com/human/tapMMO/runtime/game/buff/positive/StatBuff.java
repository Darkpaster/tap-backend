package com.human.tapMMO.runtime.game.buff.positive;

import com.human.tapMMO.runtime.game.actor.Actor;
import com.human.tapMMO.runtime.game.actor.player.Player;
import com.human.tapMMO.runtime.game.buff.Buff;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class StatBuff extends Buff {
    private String statName;
    private int statBonus;
    private int originalValue;

    public StatBuff(String name, String description, int duration, String statName, int statBonus) {
        super(name, description, duration, true);
        this.statName = statName;
        this.statBonus = statBonus;
        this.originalValue = 0;
    }

    @Override
    protected void onApply() {
        Actor target = getTarget();
        if (target instanceof Player) {
            Player player = (Player) target;
            Map<String, Integer> stats = player.getStats();

            if (stats.containsKey(statName)) {
                originalValue = stats.get(statName);
                stats.put(statName, originalValue + statBonus);
            }
        }
    }

    @Override
    protected void onRemove() {
        Actor target = getTarget();
        if (target instanceof Player) {
            Player player = (Player) target;
            Map<String, Integer> stats = player.getStats();

            if (stats.containsKey(statName)) {
                stats.put(statName, originalValue);
            }
        }
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // Этот бафф не делает ничего во время обновления
    }
}
