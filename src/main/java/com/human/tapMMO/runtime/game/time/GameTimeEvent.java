package com.human.tapMMO.runtime.game.time;

import org.springframework.context.ApplicationEvent;

public class GameTimeEvent extends ApplicationEvent {
    private GameDateTime gameDateTime;
    private GameTimeEventType type;

    public GameTimeEvent(Object source, GameDateTime gameDateTime, GameTimeEventType type) {
        super(source);
        this.gameDateTime = gameDateTime;
        this.type = type;
    }

    public GameDateTime getGameDateTime() {
        return gameDateTime;
    }

    public GameTimeEventType getType() {
        return type;
    }
}

