package com.human.tapMMO.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    private Long entityId;
    private int x;
    private int y;
    private String renderState;
}
