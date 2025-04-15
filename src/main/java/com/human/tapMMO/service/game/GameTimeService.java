package com.human.tapMMO.service.game;

import com.human.tapMMO.runtime.game.time.*;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameTimeService {
    // Получение текущего времени
    @Getter
    private GameDateTime currentTime;
    private Map<String, ScheduledGameEvent> scheduledEvents;
    private float timeScale; // Множитель скорости течения времени
    private boolean isPaused;

    private final ApplicationEventPublisher eventPublisher;

    public GameTimeService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;

        // Инициализация начального времени (например, 1000 год, 1 месяц, 1 день, 8 утра)
        this.currentTime = new GameDateTime(1000, 1, 1, 8, 0);
        this.scheduledEvents = new ConcurrentHashMap<>();
        this.timeScale = 1.0f; // По умолчанию 1 минута реального времени = 1 минута игрового времени
        this.isPaused = false;
    }

    // Запускается каждую минуту в реальном времени
    @Scheduled(fixedRate = 60000) // 60000 мс = 1 минута
    public void updateGameTime() {
        if (isPaused) {
            return;
        }

        // Определяем, сколько игровых минут должно пройти
        int minutesToAdd = Math.max(1, Math.round(timeScale));

        for (int i = 0; i < minutesToAdd; i++) {
            // Сохраняем старые значения для определения изменений
            Season oldSeason = currentTime.getCurrentSeason();
            DayPhase oldPhase = currentTime.getCurrentPhase();
            Weather oldWeather = currentTime.getCurrentWeather();
            int oldHour = currentTime.getHour();
            int oldDay = currentTime.getDay();
            int oldMonth = currentTime.getMonth();
            int oldYear = currentTime.getYear();

            // Увеличиваем игровое время на одну минуту
            currentTime.incrementMinute();

            // Публикуем событие минуты
            eventPublisher.publishEvent(new GameTimeEvent(this, currentTime, GameTimeEventType.MINUTE_PASSED));

            // Проверяем изменение часа
            if (currentTime.getHour() != oldHour) {
                eventPublisher.publishEvent(new GameTimeEvent(this, currentTime, GameTimeEventType.HOUR_PASSED));

                // Проверяем изменение фазы дня
                if (currentTime.getCurrentPhase() != oldPhase) {
                    GameTimeEventType phaseEvent = null;
                    switch (currentTime.getCurrentPhase()) {
                        case DAWN:
                            phaseEvent = GameTimeEventType.DAWN;
                            break;
                        case MORNING:
                            phaseEvent = GameTimeEventType.MORNING;
                            break;
                        case NOON:
                            phaseEvent = GameTimeEventType.NOON;
                            break;
                        case AFTERNOON:
                            phaseEvent = GameTimeEventType.AFTERNOON;
                            break;
                        case EVENING:
                            phaseEvent = GameTimeEventType.EVENING;
                            break;
                        case NIGHT:
                            if (currentTime.getHour() == 0) {
                                phaseEvent = GameTimeEventType.MIDNIGHT;
                            }
                            break;
                    }

                    if (phaseEvent != null) {
                        eventPublisher.publishEvent(new GameTimeEvent(this, currentTime, phaseEvent));
                    }
                }
            }

            // Проверяем изменение дня
            if (currentTime.getDay() != oldDay) {
                eventPublisher.publishEvent(new GameTimeEvent(this, currentTime, GameTimeEventType.DAY_PASSED));

                // Проверяем, прошла ли неделя (по воскресеньям)
                if (getDayOfWeek() == 7) {
                    eventPublisher.publishEvent(new GameTimeEvent(this, currentTime, GameTimeEventType.WEEK_PASSED));
                }
            }

            // Проверяем изменение месяца
            if (currentTime.getMonth() != oldMonth) {
                eventPublisher.publishEvent(new GameTimeEvent(this, currentTime, GameTimeEventType.MONTH_PASSED));
            }

            // Проверяем изменение года
            if (currentTime.getYear() != oldYear) {
                eventPublisher.publishEvent(new GameTimeEvent(this, currentTime, GameTimeEventType.YEAR_PASSED));
            }

            // Проверяем изменение сезона
            if (currentTime.getCurrentSeason() != oldSeason) {
                eventPublisher.publishEvent(new GameTimeEvent(this, currentTime, GameTimeEventType.SEASON_CHANGED));
            }

            // Проверяем погоду (предположим, погода случайно меняется)
            if (currentTime.getCurrentWeather() != oldWeather) {
                eventPublisher.publishEvent(new GameTimeEvent(this, currentTime, GameTimeEventType.WEATHER_CHANGED));
            }

            // Проверяем и выполняем запланированные события
            checkScheduledEvents();
        }
    }

    // Определение дня недели (1 = понедельник, 7 = воскресенье)
    private int getDayOfWeek() {
        // Базовое вычисление дня недели по алгоритму Зеллера
        int q = currentTime.getDay();
        int m = currentTime.getMonth();
        int K = currentTime.getYear() % 100;
        int J = currentTime.getYear() / 100;

        if (m <= 2) {
            m += 12;
            K--;
        }

        int h = (q + (13 * (m + 1)) / 5 + K + K/4 + J/4 - 2*J) % 7;

        // Преобразуем в формат, где 1 = понедельник, 7 = воскресенье
        return h == 0 ? 7 : h;
    }

    // Проверка и выполнение запланированных событий
    private void checkScheduledEvents() {
        List<ScheduledGameEvent> eventsToRemove = new ArrayList<>();
        List<ScheduledGameEvent> eventsToUpdate = new ArrayList<>();

        for (ScheduledGameEvent event : scheduledEvents.values()) {
            if (event.shouldTrigger(currentTime)) {
                // Выполнение действия события
                try {
                    event.getAction().run();
                } catch (Exception e) {
                    // Логирование ошибки
                    System.err.println("Error executing game event: " + event.getId() + " - " + e.getMessage());
                }

                // Если событие повторяющееся, обновляем время следующего запуска
                if (event.isRepeating()) {
                    eventsToUpdate.add(event);
                } else {
                    // Если не повторяющееся, удаляем из списка
                    eventsToRemove.add(event);
                }
            }
        }

        // Обновляем повторяющиеся события
        for (ScheduledGameEvent event : eventsToUpdate) {
            event.updateNextTriggerTime(currentTime);
        }

        // Удаляем выполненные неповторяющиеся события
        for (ScheduledGameEvent event : eventsToRemove) {
            scheduledEvents.remove(event.getId());
        }
    }

    // Регистрация нового запланированного события
    public void scheduleEvent(ScheduledGameEvent event) {
        scheduledEvents.put(event.getId(), event);
    }

    // Отмена запланированного события
    public boolean cancelEvent(String eventId) {
        return scheduledEvents.remove(eventId) != null;
    }

    // Управление временем
    public void setTimeScale(float scale) {
        if (scale >= 0.1f && scale <= 100.0f) {
            this.timeScale = scale;
        } else {
            throw new IllegalArgumentException("Time scale must be between 0.1 and 100");
        }
    }

    public void pauseTime() {
        this.isPaused = true;
    }

    public void resumeTime() {
        this.isPaused = false;
    }

    public void setGameTime(int year, int month, int day, int hour, int minute) {
        this.currentTime = new GameDateTime(year, month, day, hour, minute);
    }

    // Расчет разницы между двумя игровыми временами
    public long calculateTimeDifference(GameDateTime time1, GameDateTime time2, ChronoUnit unit) {
        // Преобразуем игровое время в объекты LocalDateTime для использования встроенных функций Java
        LocalDateTime dateTime1 = LocalDateTime.of(
                time1.getYear(), time1.getMonth(), time1.getDay(), time1.getHour(), time1.getMinute());

        LocalDateTime dateTime2 = LocalDateTime.of(
                time2.getYear(), time2.getMonth(), time2.getDay(), time2.getHour(), time2.getMinute());

        return unit.between(dateTime1, dateTime2);
    }

    // Добавление времени к игровому времени
    public GameDateTime addTime(GameDateTime baseTime, long amount, ChronoUnit unit) {
        LocalDateTime dateTime = LocalDateTime.of(
                baseTime.getYear(), baseTime.getMonth(), baseTime.getDay(), baseTime.getHour(), baseTime.getMinute());

        LocalDateTime newDateTime = dateTime.plus(amount, unit);

        return new GameDateTime(
                newDateTime.getYear(),
                newDateTime.getMonthValue(),
                newDateTime.getDayOfMonth(),
                newDateTime.getHour(),
                newDateTime.getMinute()
        );
    }

    // Создание простого события, которое выполняется один раз
    public ScheduledGameEvent createOneTimeEvent(String name, String description,
                                                 GameDateTime time, GameEventType type, Runnable action) {
        String id = UUID.randomUUID().toString();
        return new ScheduledGameEvent(id, name, description, time, type, false, 0, action);
    }

    // Создание повторяющегося события
    public ScheduledGameEvent createRepeatingEvent(String name, String description,
                                                   GameDateTime firstTime, GameEventType type,
                                                   int repeatIntervalMinutes, Runnable action) {
        String id = UUID.randomUUID().toString();
        return new ScheduledGameEvent(id, name, description, firstTime, type, true, repeatIntervalMinutes, action);
    }
}