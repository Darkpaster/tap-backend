package com.human.tapMMO.runtime.game.skills.passive;


import com.human.tapMMO.runtime.game.actors.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public abstract class PassiveAbility {
    private long id;
    private String name;
    private String description;
    private String iconPath;
    private boolean isActive;
    private Player owner;

    // Условия активации
    private int requiredLevel;
    private String requiredSkill;
    private int requiredSkillLevel;

    // Модификаторы статов
    private final Map<String, Integer> statModifiers;
    private final Map<String, Double> percentageModifiers;

    public PassiveAbility(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = false;
        this.statModifiers = new HashMap<>();
        this.percentageModifiers = new HashMap<>();
    }

    /**
     * Проверяет условия активации способности
     */
    public boolean checkActivationConditions() {
        if (owner == null) return false;

        // Проверка уровня
        if (owner.getLevel() < requiredLevel) return false;

        // Здесь можно добавить другие проверки

        return true;
    }

    /**
     * Активирует пассивную способность
     */
    public void activate() {
        if (checkActivationConditions() && !isActive) {
            isActive = true;
            onActivate();
        }
    }

    /**
     * Деактивирует пассивную способность
     */
    public void deactivate() {
        if (isActive) {
            isActive = false;
            onDeactivate();
        }
    }

    /**
     * Применяет модификаторы статов
     */
    public void applyStatModifiers(Map<String, Integer> playerStats) {
        if (!isActive) return;

        // Применение абсолютных модификаторов
        for (Map.Entry<String, Integer> modifier : statModifiers.entrySet()) {
            String statName = modifier.getKey();
            int bonus = modifier.getValue();

            if (playerStats.containsKey(statName)) {
                playerStats.put(statName, playerStats.get(statName) + bonus);
            }
        }

        // Применение процентных модификаторов
        for (Map.Entry<String, Double> modifier : percentageModifiers.entrySet()) {
            String statName = modifier.getKey();
            double bonusPercent = modifier.getValue();

            if (playerStats.containsKey(statName)) {
                int currentValue = playerStats.get(statName);
                int bonusValue = (int) (currentValue * bonusPercent / 100.0);
                playerStats.put(statName, currentValue + bonusValue);
            }
        }
    }

    /**
     * Добавляет модификатор стата
     */
    public void addStatModifier(String statName, int bonus) {
        statModifiers.put(statName, bonus);
    }

    /**
     * Добавляет процентный модификатор стата
     */
    public void addPercentageModifier(String statName, double percent) {
        percentageModifiers.put(statName, percent);
    }

    /**
     * Вызывается при активации способности
     */
    protected abstract void onActivate();

    /**
     * Вызывается при деактивации способности
     */
    protected abstract void onDeactivate();

    /**
     * Вызывается при смерти владельца
     */
    public void onOwnerDeath() {
        // Базовая реализация - ничего не делает
        // Переопределить в подклассах при необходимости
    }
}
