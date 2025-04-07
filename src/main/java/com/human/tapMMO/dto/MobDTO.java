package com.human.tapMMO.dto;


import com.human.tapMMO.model.Position;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Data
public class MobDTO {
    private int id;
    private int x;
    private int y;
    private int health;

//    private Position target;

    public MobDTO update(HashMap<Long, Position> players) {
        for (Position player: players.values()) {
            if (getDistance(player.getX(), player.getY(), x, y) < 100) {

            }
        }
        return this;
    }

    private int getDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}
