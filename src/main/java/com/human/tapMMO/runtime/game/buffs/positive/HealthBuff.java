package com.human.tapMMO.runtime.game.buffs.positive;

import com.human.tapMMO.runtime.game.actors.Actor;
import com.human.tapMMO.runtime.game.buffs.Buff;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthBuff extends Buff {
    private int healthBonus;
    private int maxHealthBonus;

    public HealthBuff(String name, String description, int duration, int healthBonus, int maxHealthBonus) {
        super(name, description, duration, true);
        this.healthBonus = healthBonus;
        this.maxHealthBonus = maxHealthBonus;
    }

    @Override
    protected void onApply() {
        Actor target = getTarget();
        if (target != null) {
            target.setMaxHealth(target.getMaxHealth() + maxHealthBonus);
            target.heal(healthBonus);
        }
    }

    @Override
    protected void onRemove() {
        Actor target = getTarget();
        if (target != null) {
            int newMaxHealth = target.getMaxHealth() - maxHealthBonus;
            target.setMaxHealth(newMaxHealth);

            // Убедимся, что текущее здоровье не превышает новый максимум
            if (target.getHealth() > newMaxHealth) {
                target.setHealth(newMaxHealth);
            }
        }
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // Этот бафф не делает ничего во время обновления
    }

    // Геттеры и сеттеры
    public int getHealthBonus() {
        return healthBonus;
    }

    public int getMaxHealthBonus() {
        return maxHealthBonus;
    }
}
