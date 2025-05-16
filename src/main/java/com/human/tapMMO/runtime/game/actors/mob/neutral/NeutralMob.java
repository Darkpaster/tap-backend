package com.human.tapMMO.runtime.game.actors.mob.neutral;

import com.human.tapMMO.runtime.game.actors.Actor;
import com.human.tapMMO.runtime.game.actors.mob.Mob;
import com.human.tapMMO.runtime.game.actors.mob.MobState;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class NeutralMob extends Mob {
    private Actor lastAttacker;
    private int peacefulTimer;

    public NeutralMob() {
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
    public <T extends Actor> Mob update(HashMap<Long, T> players) {
        updatePeacefulTimer(1); // Предполагаем, что метод вызывается каждую секунду
        super.update(players);
        return this;
    }

    @Override
    public void onDeath() {
        super.onDeath();
        // Специфичная логика смерти нейтрального моба
    }
}
