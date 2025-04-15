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

// Тип события
public enum GameTimeEventType {
    MINUTE_PASSED, HOUR_PASSED, DAY_PASSED, WEEK_PASSED, MONTH_PASSED, YEAR_PASSED,
    DAWN, MORNING, NOON, AFTERNOON, EVENING, MIDNIGHT,
    SEASON_CHANGED, WEATHER_CHANGED
}

