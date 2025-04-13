package com.human.tapMMO.runtime.game.actor.mob.enemy;

import com.human.tapMMO.runtime.game.actor.Actor;
import com.human.tapMMO.runtime.game.actor.mob.Mob;
import com.human.tapMMO.runtime.game.actor.mob.MobState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Enemy extends Mob {
    private boolean isFleeing;
    private int fleeHealthThreshold;

    public Enemy(String name, int health, int level, int aggroRange, int experienceValue) {
        super(name, health, level, aggroRange, experienceValue);
        this.isFleeing = false;
        this.fleeHealthThreshold = health / 5; // Моб начинает убегать при 20% здоровья
    }

    @Override
    public void attack(Actor target) {
        // Базовая атака врага
        int damage = getLevel() * 5;
        target.takeDamage(damage);
    }

    @Override
    public void updateAI() {
        // Проверка условия для бегства
        if (getHealth() < fleeHealthThreshold && !isFleeing) {
            isFleeing = true;
            // Логика бегства
        }

        // Если враг убегает, логика движения отличается
        if (isFleeing) {
            fleeBehavior();
        } else {
            super.updateAI();
        }
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
