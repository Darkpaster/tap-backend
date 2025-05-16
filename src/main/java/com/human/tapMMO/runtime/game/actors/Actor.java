package com.human.tapMMO.runtime.game.actors;

import lombok.Getter;
import lombok.Setter;

/**
 * Базовый класс для всех существ в игре
 */
@Getter
@Setter
public abstract class Actor {
    private long id;
    protected String name;
    protected int health;
    protected int maxHealth;
    protected byte speed = 2;
    protected String renderState = ActorRenderStates.IDLE;
    protected Actor target = null;
    protected int level;
    protected boolean alive;
    protected float x;
    protected float y;

    public Actor() {
        this.name = "Unknown";
        this.health = 100;
        this.maxHealth = health;
        this.level = 1;
        this.alive = true;
    }

    public void takeDamage(int damage) {
        if (!isAlive()) return;

        health -= damage;
        if (health <= 0) {
            health = 0;
            this.renderState = ActorRenderStates.DEATH;
            alive = false;
            onDeath();
        }
    }

    public void heal(int amount) {
        if (!isAlive()) return;

        health = Math.min(health + amount, maxHealth);
    }

    public abstract void onDeath();
}