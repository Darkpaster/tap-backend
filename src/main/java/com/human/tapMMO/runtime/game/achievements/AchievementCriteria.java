package com.human.tapMMO.runtime.game.achievements;

import lombok.Data;

@Data
public class AchievementCriteria {
    private String criteriaType;
    private String objectiveId;
    private int requiredCount;
    private String description;
}
