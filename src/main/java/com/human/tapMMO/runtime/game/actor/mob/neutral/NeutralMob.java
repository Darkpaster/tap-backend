package com.human.tapMMO.runtime.game.actor.mob.neutral;

import com.human.tapMMO.runtime.game.actor.Actor;
import com.human.tapMMO.runtime.game.actor.mob.Mob;
import com.human.tapMMO.runtime.game.actor.mob.MobState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NeutralMob extends Mob {
    private Actor lastAttacker;
    private int peacefulTimer;

    public NeutralMob(String name, int health, int level, int aggroRange, int experienceValue) {
        super(name, health, level, aggroRange, experienceValue);
        this.lastAttacker = null;
        this.peacefulTimer = 0;
    }

    @Override
    public void attack(Actor target) {
        // Базовая атака нейтрального моба
        int damage = getLevel() * 3;
        target.takeDamage(damage);
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        // При получении урона нейтральный моб становится агрессивным
        if (getState() != MobState.AGGRO && lastAttacker != null) {
            setState(MobState.AGGRO);
            peacefulTimer = 30; // 30 секунд агрессии
        }
    }

    public void updatePeacefulTimer(int deltaTime) {
        if (getState() == MobState.AGGRO && peacefulTimer > 0) {
            peacefulTimer -= deltaTime;
            if (peacefulTimer <= 0) {
                setState(MobState.IDLE);
                lastAttacker = null;
            }
        }
    }

    @Override
    public void updateAI() {
        updatePeacefulTimer(1); // Предполагаем, что метод вызывается каждую секунду
        super.updateAI();
    }

    @Override
    public void onDeath() {
        super.onDeath();
        // Специфичная логика смерти нейтрального моба
    }
}
