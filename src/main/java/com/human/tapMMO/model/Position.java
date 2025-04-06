package com.human.tapMMO.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    Long entityId;
    int x;
    int y;
    String renderState;
    String entityType; //mob, player
}
