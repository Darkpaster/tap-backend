package com.human.tapMMO.runtime.game.actors.mob.enemy;

import com.human.tapMMO.runtime.game.actors.Actor;
import com.human.tapMMO.runtime.game.actors.mob.Mob;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class Enemy extends Mob {
    private boolean isFleeing;
    private int fleeHealthThreshold;

    public Enemy() {
        this.isFleeing = false;
        this.fleeHealthThreshold = this.health / 5; // Моб начинает убегать при 20% здоровья
    }

    @Override
    public void attack(Actor target) {
        // Базовая атака врага
        int damage = (int) (level * Math.random() * 5);
        target.takeDamage(damage);
    }

    @Override
    public <T extends Actor> Mob update(HashMap<Long, T> players) {
        // Проверка условия для бегства
        if (getHealth() < fleeHealthThreshold && !isFleeing) {
            isFleeing = true;
            // Логика бегства
        }

        // Если враг убегает, логика движения отличается
        if (isFleeing) {
            fleeBehavior();
        } else {
            super.update(players);
        }
        return this;
    }

    private void fleeBehavior() {
        // Логика убегания от цели
    }

    @Override
    public void onDeath() {
        super.onDeath();
        // Специфичная логика смерти врага
    }

    // Геттеры и сеттеры
    public boolean isFleeing() {
        return isFleeing;
    }

    public void setFleeing(boolean fleeing) {
        isFleeing = fleeing;
    }

}
