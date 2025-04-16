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
    private String name;
    private int health;
    private int maxHealth;
    private int level;
    private boolean alive;
//    private Position position;

    public Actor(String name, int health, int level) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.level = level;
        this.alive = true;
//        this.position = new Position(0, 0, 0);
    }

    public void takeDamage(int damage) {
        if (!isAlive()) return;

        health -= damage;
        if (health <= 0) {
            health = 0;
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