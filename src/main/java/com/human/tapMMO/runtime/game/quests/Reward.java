package com.human.tapMMO.runtime.game.quests;

import lombok.Getter;

@Getter
public class Reward {
    // Геттеры
    private String id;
    private RewardType type;
    private int amount;

    public Reward(String id, RewardType type, int amount) {
        this.id = id;
        this.type = type;
        this.amount = amount;
    }

}

// Тип награды
enum RewardType {
    EXPERIENCE, GOLD, ITEM, REPUTATION
}

// Статус квеста
public enum QuestStatus {
    NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED
}
