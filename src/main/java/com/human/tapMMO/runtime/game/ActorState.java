package com.human.tapMMO.runtime.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActorState { // для передачи по вебсокету
    Long actorId;
    int health;
    int stamina;
    String actorType; //mob, player
}
