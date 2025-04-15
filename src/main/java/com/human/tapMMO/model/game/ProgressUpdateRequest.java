package com.human.tapMMO.model.game;

import lombok.Data;

@Data
public class ProgressUpdateRequest {
    private Long playerId;
    private String objectiveType;
    private String objectiveId;
    private int increment;
}