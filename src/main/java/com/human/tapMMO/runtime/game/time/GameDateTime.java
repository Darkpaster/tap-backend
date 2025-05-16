package com.human.tapMMO.runtime.game.time;

import lombok.Getter;

import java.util.Random;

@Getter
public class GameDateTime {
    // Геттеры
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private Season currentSeason;
    private Weather currentWeather;
    private DayPhase currentPhase;

    public GameDateTime(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        updateSeason();
        updateWeather();
        updateDayPhase();
    }

    // Обновление времени на одну минуту
    public void incrementMinute() {
        minute++;
        if (minute >= 60) {
            minute = 0;
            incrementHour();
        }
    }

    // Обновление времени на один час
    public void incrementHour() {
        hour++;
        if (hour >= 24) {
            hour = 0;
            incrementDay();
        }
        updateDayPhase();
    }

    // Обновление времени на один день
    public void incrementDay() {
        day++;
        int maxDays = getMaxDaysInMonth();
        if (day > maxDays) {
            day = 1;
            incrementMonth();
        }
    }

    // Обновление времени на один месяц
    public void incrementMonth() {
        month++;
        if (month > 12) {
            month = 1;
            incrementYear();
        }
        updateSeason();
    }

    // Обновление времени на один год
    public void incrementYear() {
        year++;
    }

    // Определение текущего времени суток
    private void updateDayPhase() {
        if (hour >= 5 && hour < 8) {
            currentPhase = DayPhase.DAWN;
        } else if (hour >= 8 && hour < 12) {
            currentPhase = DayPhase.MORNING;
        } else if (hour >= 12 && hour < 14) {
            currentPhase = DayPhase.NOON;
        } else if (hour >= 14 && hour < 18) {
            currentPhase = DayPhase.AFTERNOON;
        } else if (hour >= 18 && hour < 22) {
            currentPhase = DayPhase.EVENING;
        } else {
            currentPhase = DayPhase.NIGHT;
        }
    }

    // Определение текущего сезона
    private void updateSeason() {
        if (month >= 3 && month <= 5) {
            currentSeason = Season.SPRING;
        } else if (month >= 6 && month <= 8) {
            currentSeason = Season.SUMMER;
        } else if (month >= 9 && month <= 11) {
            currentSeason = Season.AUTUMN;
        } else {
            currentSeason = Season.WINTER;
        }
    }

    // Случайная генерация погоды (упрощенно)
    private void updateWeather() {
        Random random = new Random();
        int chance = random.nextInt(100);

        switch (currentSeason) {
            case SPRING:
                if (chance < 40) {
                    currentWeather = Weather.RAINY;
                } else if (chance < 70) {
                    currentWeather = Weather.CLOUDY;
                } else {
                    currentWeather = Weather.SUNNY;
                }
                break;
            case SUMMER:
                if (chance < 20) {
                    currentWeather = Weather.RAINY;
                } else if (chance < 40) {
                    currentWeather = Weather.CLOUDY;
                } else {
                    currentWeather = Weather.SUNNY;
                }
                break;
            case AUTUMN:
                if (chance < 50) {
                    currentWeather = Weather.RAINY;
                } else if (chance < 80) {
                    currentWeather = Weather.CLOUDY;
                } else {
                    currentWeather = Weather.SUNNY;
                }
                break;
            case WINTER:
                if (chance < 40) {
                    currentWeather = Weather.SNOWY;
                } else if (chance < 80) {
                    currentWeather = Weather.CLOUDY;
                } else {
                    currentWeather = Weather.SUNNY;
                }
                break;
        }
    }

    // Максимальное количество дней в месяце
    private int getMaxDaysInMonth() {
        switch (month) {
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                return isLeapYear() ? 29 : 28;
            default:
                return 31;
        }
    }

    // Проверка на високосный год
    private boolean isLeapYear() {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    // Форматирование времени
    @Override
    public String toString() {
        return String.format("Year %d, %s %d, %02d:%02d - %s (%s, %s)",
                year, getMonthName(), day, hour, minute,
                currentPhase, currentSeason, currentWeather);
    }

    private String getMonthName() {
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        return months[month - 1];
    }
}

