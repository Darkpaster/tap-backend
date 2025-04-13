package com.human.tapMMO.runtime.game.actor.mob.npc;

public enum NPCState {
    IDLE,       // В ожидании взаимодействия
    TALKING,    // Ведет диалог с игроком
    WALKING,    // Перемещается по маршруту
    WORKING,    // Выполняет работу (анимация)
    DEAD        // NPC мертв
}
