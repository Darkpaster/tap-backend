package com.human.tapMMO.runtime.game.time;

import lombok.Getter;

@Getter
public class ScheduledGameEvent {
    // Геттеры
    private String id;
    private String name;
    private String description;
    private GameDateTime scheduledTime;
    private GameEventType type;
    private boolean repeating;
    private int repeatInterval; // в минутах игрового времени
    private Runnable action;

    public ScheduledGameEvent(String id, String name, String description, GameDateTime scheduledTime,
                              GameEventType type, boolean repeating, int repeatInterval, Runnable action) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.scheduledTime = scheduledTime;
        this.type = type;
        this.repeating = repeating;
        this.repeatInterval = repeatInterval;
        this.action = action;
    }

    // Проверка, должно ли событие запуститься в указанное время
    public boolean shouldTrigger(GameDateTime currentTime) {
        return currentTime.getYear() == scheduledTime.getYear() &&
                currentTime.getMonth() == scheduledTime.getMonth() &&
                currentTime.getDay() == scheduledTime.getDay() &&
                currentTime.getHour() == scheduledTime.getHour() &&
                currentTime.getMinute() == scheduledTime.getMinute();
    }

    // Обновление времени для повторяющегося события
    public void updateNextTriggerTime(GameDateTime currentTime) {
        if (repeating) {
            // Простая логика: добавляем интервал повторения к текущему времени
            int totalMinutes = currentTime.getMinute() + repeatInterval;
            int hoursToAdd = totalMinutes / 60;
            int remainingMinutes = totalMinutes % 60;

            scheduledTime = new GameDateTime(
                    currentTime.getYear(),
                    currentTime.getMonth(),
                    currentTime.getDay(),
                    currentTime.getHour() + hoursToAdd,
                    remainingMinutes
            );
        }
    }
}

