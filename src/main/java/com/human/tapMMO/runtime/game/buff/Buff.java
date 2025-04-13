package com.human.tapMMO.runtime.game.buff;

import com.human.tapMMO.runtime.game.actor.Actor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Buff {
    private long id;
    private String name;
    private String description;
    private float duration; // в секундах
    private boolean isPermanent;
    private boolean isPositive;
    private Actor target;

    public Buff(String name, String description, int duration, boolean isPositive) {
//        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.isPermanent = (duration <= 0);
        this.isPositive = isPositive;
    }

    /**
     * Применяет бафф к цели
     * @param target цель для применения баффа
     */
    public void applyTo(Actor target) {
        this.target = target;
        onApply();
    }

    /**
     * Снимает бафф с цели
     */
    public void remove() {
        if (target != null) {
            onRemove();
            target = null;
        }
    }

    /**
     * Обновляет состояние баффа (должен вызываться каждый тик)
     * @param deltaTime прошедшее время в секундах
     * @return true если бафф все еще активен, false если его время истекло
     */
    public boolean update(float deltaTime) {
        if (isPermanent) return true;

        duration -= deltaTime;
        if (duration <= 0) {
            remove();
            return false;
        }

        onUpdate(deltaTime);
        return true;
    }

    /**
     * Действие при применении баффа
     */
    protected abstract void onApply();

    /**
     * Действие при удалении баффа
     */
    protected abstract void onRemove();

    /**
     * Действие при обновлении баффа
     */
    protected abstract void onUpdate(float deltaTime);

}
