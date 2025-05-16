package com.human.tapMMO.runtime.game.time;

import com.human.tapMMO.service.game.world.GameTimeService;

import java.time.temporal.ChronoUnit;

class GameTimeExample {

    public void configureTimeSystem(GameTimeService timeService) {
        // Пример настройки скорости времени
        // 1.0 = реальное время, 60.0 = 1 час реального времени = 60 часов игрового времени
        timeService.setTimeScale(24.0f); // 1 минута реального времени = 24 минуты игрового времени

        // Пример создания ежедневного события рынка
        GameDateTime marketOpenTime = new GameDateTime(
                timeService.getCurrentTime().getYear(),
                timeService.getCurrentTime().getMonth(),
                timeService.getCurrentTime().getDay(),
                8, // 8 утра
                0
        );

        ScheduledGameEvent dailyMarket = timeService.createRepeatingEvent(
                "Daily Market",
                "The town market opens every day at 8 AM",
                marketOpenTime,
                GameEventType.SHOP_RESTOCK,
                24 * 60, // повторять каждые 24 часа (в минутах)
                () -> {
                    // Логика открытия рынка
                    System.out.println("Market is now open!");
                    // Здесь мы бы обновляли товары у торговцев, генерировали случайные предметы и т.д.
                }
        );

        // Регистрируем событие
        timeService.scheduleEvent(dailyMarket);

        // Пример еженедельного события (например, воскресный фестиваль)
        int currentDayOfWeek = calculateDayOfWeek(timeService.getCurrentTime());
        int daysUntilSunday = 7 - currentDayOfWeek;

        GameDateTime nextSunday = timeService.addTime(
                timeService.getCurrentTime(),
                daysUntilSunday,
                ChronoUnit.DAYS
        );

        // Устанавливаем время на 12:00
        GameDateTime festivalTime = new GameDateTime(
                nextSunday.getYear(),
                nextSunday.getMonth(),
                nextSunday.getDay(),
                12, // полдень
                0
        );

        ScheduledGameEvent weeklyFestival = timeService.createRepeatingEvent(
                "Weekly Festival",
                "A festival that occurs every Sunday at noon",
                festivalTime,
                GameEventType.FESTIVAL,
                7 * 24 * 60, // повторять каждые 7 дней (в минутах)
                () -> {
                    // Логика воскресного фестиваля
                    System.out.println("Weekly festival has begun!");
                    // Здесь могли бы быть специальные квесты, NPC, торговцы и т.д.
                }
        );

        // Регистрируем событие
        timeService.scheduleEvent(weeklyFestival);

        // Пример сезонного события (например, зимний праздник)
        GameDateTime currentTime = timeService.getCurrentTime();
        GameDateTime winterFestivalTime;

        if (currentTime.getMonth() <= 12 && currentTime.getDay() <= 21) {
            // Если текущая дата до 21 декабря, устанавливаем на это число текущего года
            winterFestivalTime = new GameDateTime(
                    currentTime.getYear(),
                    12, // декабрь
                    21, // 21 число
                    18, // 6 вечера
                    0
            );
        } else {
            // Иначе устанавливаем на следующий год
            winterFestivalTime = new GameDateTime(
                    currentTime.getYear() + 1,
                    12, // декабрь
                    21, // 21 число
                    18, // 6 вечера
                    0
            );
        }

        ScheduledGameEvent winterFestival = timeService.createRepeatingEvent(
                "Winter Solstice Festival",
                "A major celebration that occurs on winter solstice",
                winterFestivalTime,
                GameEventType.FESTIVAL,
                365 * 24 * 60, // повторять каждый год (в минутах)
                () -> {
                    // Логика зимнего праздника
                    System.out.println("Winter Solstice Festival has begun!");
                    // Создаем праздничные украшения, особые игровые механики и т.д.
                }
        );

        // Регистрируем событие
        timeService.scheduleEvent(winterFestival);
    }

    // Вспомогательный метод для вычисления дня недели
    private int calculateDayOfWeek(GameDateTime time) {
        // Формула Зеллера для определения дня недели
        int q = time.getDay();
        int m = time.getMonth();
        int y = time.getYear();

        if (m <= 2) {
            m += 12;
            y--;
        }

        int k = y % 100;
        int j = y / 100;

        int h = (q + 13*(m+1)/5 + k + k/4 + j/4 + 5*j) % 7;

        // Преобразование в формат 1=понедельник, 7=воскресенье
        return ((h + 5) % 7) + 1;
    }
}
