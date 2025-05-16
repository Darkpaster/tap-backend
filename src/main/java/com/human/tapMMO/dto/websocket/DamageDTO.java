package com.human.tapMMO.dto.websocket;

import lombok.Data;
import lombok.Getter;

@Data
public class DamageDTO {
    private int value;
    private Target target;

    @Getter
    public class Target {
        private String targetType;
        private long targetId;
    }
}
