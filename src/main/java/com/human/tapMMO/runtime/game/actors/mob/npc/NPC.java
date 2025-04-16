package com.human.tapMMO.runtime.game.actors.mob.npc;

import com.human.tapMMO.runtime.game.actors.Actor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NPC extends Actor {
    private NPCState state;
    private String[] dialogue;
    private boolean questGiver;
    private boolean merchant;

    public NPC(String name, int health, int level, String[] dialogue) {
        super(name, health, level);
        this.state = NPCState.IDLE;
        this.dialogue = dialogue;
        this.questGiver = false;
        this.merchant = false;
    }

    public String interact() {
        // Базовая логика взаимодействия с NPC
        return dialogue[0];
    }

    public String getDialogue(int index) {
        if (index >= 0 && index < dialogue.length) {
            return dialogue[index];
        }
        return "...";
    }

    @Override
    public void onDeath() {
        setState(NPCState.DEAD);
        // Специфичная логика смерти NPC
    }
}