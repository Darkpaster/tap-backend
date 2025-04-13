package com.human.tapMMO.runtime.game.actor.mob;

/**
 * Состояния моба
 */
public enum MobState {
    IDLE,       // В покое, патрулирование
    AGGRO,      // Агрессивное состояние, атакует цель
    RETURNING,  // Возвращение на исходную позицию
    DEAD        // Моб мертв
}
