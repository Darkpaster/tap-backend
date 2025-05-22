package com.human.tapMMO.util.time;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Класс для реализации таймера с обратным вызовом.
 * Используется для выполнения действий после указанной задержки и отслеживания перезарядки.
 */
@Data
@NoArgsConstructor
public class CallbackTimer {
    /**
     * -- SETTER --
     *  Устанавливает новую функцию обратного вызова.
     *
     * @param callback новая функция обратного вызова
     */
    @Setter
    private Runnable callback;
    @Setter
    private long delay;
    private ScheduledFuture<?> scheduledTask;
    private CallbackTimer cooldown;
    private boolean done;
    private long startTime;
    private ScheduledExecutorService scheduler;

    /**
     * Создает объект таймера с обратным вызовом с указанными параметрами.
     *
     * @param callback функция, которая будет вызвана по истечении таймера
     * @param delay время задержки в миллисекундах
     * @param cooldown таймер перезарядки, связанный с этим таймером
     * @param scheduler планировщик для выполнения задач
     */
    public CallbackTimer(Runnable callback, long delay, CallbackTimer cooldown, ScheduledExecutorService scheduler) {
        this.callback = callback != null ? callback : () -> System.out.println("default");
        this.delay = delay;
        this.scheduledTask = null;
        this.cooldown = cooldown;
        this.done = false;
        this.startTime = 0;
        this.scheduler = scheduler;
    }

    /**
     * Создает объект таймера с обратным вызовом с указанными параметрами без таймера перезарядки.
     *
     * @param callback функция, которая будет вызвана по истечении таймера
     * @param delay время задержки в миллисекундах
     * @param scheduler планировщик для выполнения задач
     */
    public CallbackTimer(Runnable callback, long delay, ScheduledExecutorService scheduler) {
        this(callback, delay, null, scheduler);
    }

    /**
     * Запускает таймер.
     * Если связанный таймер перезарядки активен и не завершен, таймер не запускается.
     *
     * @param initCallback функция, которая будет вызвана при запуске таймера (опционально)
     */
    public void start(Runnable initCallback) {
        if (cooldown != null && cooldown.getScheduledTask() != null && !cooldown.isDone()) {
            return;
        }

        done = false;
        startTime = System.currentTimeMillis();

        if (initCallback != null) {
            initCallback.run();
        }

        scheduledTask = scheduler.schedule(() -> {
            callback.run();
            done = true;
            stop();
        }, delay, TimeUnit.MILLISECONDS);

        if (cooldown != null) {
            cooldown.restart();
        }
    }

    /**
     * Запускает таймер без начальной функции обратного вызова.
     */
    public void start() {
        start(null);
    }

    /**
     * Останавливает таймер.
     */
    private void stop() {
        if (scheduledTask != null && !scheduledTask.isDone()) {
            scheduledTask.cancel(false);
            scheduledTask = null;
        }
    }

    /**
     * Перезапускает таймер.
     */
    public void restart() {
        stop();
        start();
    }

    /**
     * Возвращает оставшееся время таймера в миллисекундах.
     *
     * @return оставшееся время в миллисекундах
     */
    public long getLeftTime() {
        return delay - (System.currentTimeMillis() - startTime);
    }

    /**
     * Возвращает процент оставшегося времени таймера.
     *
     * @return процент оставшегося времени (от 0.0 до 1.0)
     */
    public double getLeftTimePercent() {
        return (double) (delay - (System.currentTimeMillis() - startTime)) / delay;
    }
}
