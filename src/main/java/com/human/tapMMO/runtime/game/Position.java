package com.human.tapMMO.runtime.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Position {
    private Long entityId;
    private int x;
    private int y;
    private String renderState;
}
