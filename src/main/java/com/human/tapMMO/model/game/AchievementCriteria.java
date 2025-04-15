package com.human.tapMMO.model.game;

import lombok.Data;

@Data
public class AchievementCriteria {
    private String criteriaType;
    private String objectiveId;
    private int requiredCount;
    private String description;
}
