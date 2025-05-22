package com.human.tapMMO.util.time;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс для реализации задержки на основе системного времени.
 * Используется для отслеживания временных интервалов в миллисекундах.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeDelay {
    private long delayTime;
    private Long startTime;
    private boolean atStartNoDelay;

    /**
     * Создает объект временной задержки с указанным временем задержки в миллисекундах.
     *
     * @param delayTime время задержки в миллисекундах
     * @param atStartNoDelay если true, то при первом вызове timeIsUp() сразу вернет true
     */
    public TimeDelay(long delayTime, boolean atStartNoDelay) {
        this.delayTime = delayTime;
        this.startTime = null;
        this.atStartNoDelay = atStartNoDelay;
    }

    /**
     * Создает объект временной задержки с указанным временем задержки в миллисекундах
     * и отключенной начальной задержкой.
     *
     * @param delayTime время задержки в миллисекундах
     */
    public TimeDelay(long delayTime) {
        this(delayTime, false);
    }

    /**
     * Проверяет, истекло ли время задержки.
     *
     * @return true, если время задержки истекло, иначе null
     */
    public Boolean timeIsUp() {
        if (startTime == null) {
            startTime = System.currentTimeMillis();
            if (atStartNoDelay) {
                return true;
            }
        }

        if (System.currentTimeMillis() - startTime > delayTime) {
            startTime = null;
            return !atStartNoDelay;
        }

        return false;
    }

    /**
     * Устанавливает новое время задержки.
     *
     * @param delayTime новое время задержки в миллисекундах
     */
    public void setDelay(long delayTime) {
        this.delayTime = delayTime;
    }

    /**
     * Сбрасывает время начала задержки.
     */
    public void reset() {
        this.startTime = null;
    }

    /**
     * Возвращает оставшееся время задержки в миллисекундах.
     *
     * @return оставшееся время в миллисекундах или null, если задержка не активна
     */
    public Long getLeftTime() {
        if (startTime == null) {
            return null;
        }
        return delayTime - (System.currentTimeMillis() - startTime);
    }

    /**
     * Возвращает процент оставшегося времени задержки.
     *
     * @return процент оставшегося времени (от 0.0 до 1.0) или null, если задержка не активна
     */
    public Double getLeftTimePercent() {
        Long leftTime = getLeftTime();
        if (leftTime == null) {
            return null;
        }
        return (double) leftTime / delayTime;
    }
}