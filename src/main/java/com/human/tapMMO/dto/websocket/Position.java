package com.human.tapMMO.dto.websocket;

import lombok.Data;

@Data
public class Position {
    private Long entityId;
    private int x;
    private int y;
    private String renderState;
}
