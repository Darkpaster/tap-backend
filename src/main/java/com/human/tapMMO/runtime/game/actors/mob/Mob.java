package com.human.tapMMO.runtime.game.actors.mob;

import com.human.tapMMO.runtime.game.actors.Actor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Mob extends Actor {
    // Геттеры и сеттеры
    private int aggroRange;
    private MobState state;
    private int experienceValue;

    public Mob(String name, int health, int level, int aggroRange, int experienceValue) {
        super(name, health, level);
        this.aggroRange = aggroRange;
        this.state = MobState.IDLE;
        this.experienceValue = experienceValue;
    }

    public abstract void attack(Actor target);

    public void updateAI() {
        // Базовая логика ИИ для мобов
        switch (state) {
            case MobState.IDLE:
                // Поиск целей поблизости
                break;
            case MobState.AGGRO:
                // Движение к цели и атака
                break;
            case MobState.RETURNING:
                // Возвращение на исходную позицию
                break;
            case MobState.DEAD:
                // Ничего не делать, моб мертв
                break;
        }
    }

    @Override
    public void onDeath() {
        setState(MobState.DEAD);
        // Логика при смерти моба (выпадение добычи и т.д.)
    }

    public void setAggroRange(int aggroRange) {
        this.aggroRange = aggroRange;
    }

    public void setState(MobState state) {
        this.state = state;
    }

    public void setExperienceValue(int experienceValue) {
        this.experienceValue = experienceValue;
    }
}
