package com.human.tapMMO.model.game;

public enum AchievementRarity {
    COMMON(10),
    UNCOMMON(20),
    RARE(50),
    EPIC(100),
    LEGENDARY(200);

    private final int points;

    AchievementRarity(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }
}
