package com.human.tapMMO.runtime.game.buff.negative;

import com.human.tapMMO.runtime.game.buff.Buff;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DamageOverTimeBuff extends Buff {
    private int damagePerTick;
    private float tickInterval; // в секундах
    private float timeSinceLastTick;
    private String damageType; // например, "poison", "fire", "bleed"

    public DamageOverTimeBuff(String name, String description, int duration,
                              int damagePerTick, float tickInterval, String damageType) {
        super(name, description, duration, false);
        this.damagePerTick = damagePerTick;
        this.tickInterval = tickInterval;
        this.timeSinceLastTick = 0;
        this.damageType = damageType;
    }

    @Override
    protected void onApply() {
        // Ничего не делает при наложении
    }

    @Override
    protected void onRemove() {
        // Ничего не делает при снятии
    }

    @Override
    protected void onUpdate(float deltaTime) {
        timeSinceLastTick += deltaTime;

        if (timeSinceLastTick >= tickInterval) {
            Actor target = getTarget();
            if (target != null && target.isAlive()) {
                target.takeDamage(damagePerTick);

                // Визуальный эффект повреждения (в реальной реализации)
                // effectManager.createDamageEffect(target.getPosition(), damageType);
            }

            timeSinceLastTick -= tickInterval;
        }
    }
}

