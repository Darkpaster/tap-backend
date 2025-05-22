package com.human.tapMMO.runtime.game.actors.mob.boss;

import com.human.tapMMO.runtime.game.actors.Actor;
import com.human.tapMMO.runtime.game.actors.mob.Mob;

import java.util.ArrayList;
import java.util.List;

public class Boss extends Mob {
    private List<Ability> abilities;
    private int phaseCounter;
    private int enrageTimer;
    private boolean enraged;

    public Boss() {
        super();
        this.abilities = new ArrayList<>();
        this.phaseCounter = 1;
        this.enrageTimer = 300; // 5 минут до ярости
        this.enraged = false;
    }

    @Override
    public void attack(Actor target) {
        // Базовая атака босса
        int damage = calculateDamage();
        target.takeDamage(damage);
    }

    public void useAbility(Ability ability, Actor target) {
        // Использование особой способности
        ability.execute(this, target);
        ability.startCooldown();
    }

    public void checkPhaseTransition() {
        // Проверка условий для перехода в следующую фазу
        int healthPercentage = (getHealth() * 100) / getMaxHealth();
        if (healthPercentage < 75 && phaseCounter == 1) {
            phaseCounter = 2;
            triggerPhaseChange();
        } else if (healthPercentage < 40 && phaseCounter == 2) {
            phaseCounter = 3;
            triggerPhaseChange();
        } else if (healthPercentage < 15 && phaseCounter == 3) {
            phaseCounter = 4;
            triggerPhaseChange();
        }
    }

    private void triggerPhaseChange() {
        // Выполнение действий при смене фазы босса
        // Например, восстановление части здоровья, усиление, призыв приспешников
    }

    public void updateEnrageTimer(int deltaTime) {
        if (enraged) return;

        enrageTimer -= deltaTime;
        if (enrageTimer <= 0) {
            enrage();
        }
    }

    private void enrage() {
        enraged = true;
        // Увеличение урона и скорости босса
    }

    private int calculateDamage() {
        int baseDamage = getLevel() * 10;
        return enraged ? baseDamage * 2 : baseDamage;
    }

    @Override
    public void onDeath() {
        super.onDeath();
        // Специальная логика для смерти босса (особая добыча, достижения и т.д.)
    }

    // Внутренний класс для способностей
    public static class Ability {
        private String name;
        private int damage;
        private int cooldown;
        private int currentCooldown;

        public Ability(String name, int damage, int cooldown) {
            this.name = name;
            this.damage = damage;
            this.cooldown = cooldown;
            this.currentCooldown = 0;
        }

        public void execute(Boss source, Actor target) {
            if (currentCooldown > 0) return;

            target.takeDamage(damage);
            // Дополнительные эффекты
        }

        public void startCooldown() {
            currentCooldown = cooldown;
        }

        public void updateCooldown(int deltaTime) {
            if (currentCooldown > 0) {
                currentCooldown = Math.max(0, currentCooldown - deltaTime);
            }
        }

        // Геттеры и сеттеры
        public String getName() {
            return name;
        }

        public int getDamage() {
            return damage;
        }

        public int getCooldown() {
            return cooldown;
        }

        public int getCurrentCooldown() {
            return currentCooldown;
        }
    }
}

