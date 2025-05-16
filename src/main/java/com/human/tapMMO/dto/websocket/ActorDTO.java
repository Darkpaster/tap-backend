package com.human.tapMMO.dto.websocket;

import com.human.tapMMO.runtime.game.buffs.Buff;
import lombok.Data;

@Data
public class ActorDTO {
    private Long actorId;
    private float x;
    private float y;
    private String renderState;
    private int health;
    private Buff[] buffs;
}
