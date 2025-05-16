package com.human.tapMMO.service.game.events;

import com.human.tapMMO.runtime.game.time.GameDateTime;
import com.human.tapMMO.runtime.game.time.GameTimeEvent;
import com.human.tapMMO.runtime.game.time.GameTimeEventType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
class GameTimeEventListener {

    @EventListener
    public void handleGameTimeEvent(GameTimeEvent event) {
        GameDateTime time = event.getGameDateTime();

        switch (event.getType()) {
            case HOUR_PASSED:
                // Реализация логики для каждого часа
                System.out.println("Hour passed: " + time);
                break;

            case DAY_PASSED:
                // Реализация логики для каждого дня (обновление квестов, торговцев и т.д.)
                System.out.println("Day passed: " + time);
                break;

            case SEASON_CHANGED:
                // Реализация логики смены сезонов (изменение ландшафта, событий)
                System.out.println("Season changed to: " + time.getCurrentSeason());
                break;

            case DAWN:
                // Логика рассвета (пробуждение НПС, изменение видимости)
                System.out.println("Dawn has arrived");
                break;

            case GameTimeEventType.MIDNIGHT:
                // Логика ночи (появление монстров, сон НПС)
                System.out.println("Night has fallen");
                break;

            case WEATHER_CHANGED:
                // Логика изменения погоды
                System.out.println("Weather changed to: " + time.getCurrentWeather());
                break;
        }
    }
}

