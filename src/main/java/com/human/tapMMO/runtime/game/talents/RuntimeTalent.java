package com.human.tapMMO.runtime.game.talents;

import com.human.tapMMO.runtime.game.actors.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class RuntimeTalent {
    private long id;
    private String name;
    private String description;
    private String iconPath;
    private int tier; // Уровень таланта (1-5)
    private int maxTier;
    private boolean isActive;
    private Player owner;

    // Модификаторы статов
    private final Map<String, Integer> statModifiers;
    private final Map<String, Double> percentageModifiers;

    // Требования для изучения
    private int requiredLevel;
    private String requiredSkill;
    private int requiredSkillLevel;
    private String prerequisiteTalent;

    // Специальные эффекты
    private TalentEffect specialEffect;

    public RuntimeTalent(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tier = 0;
        this.maxTier = 5;
        this.isActive = false;
        this.statModifiers = new HashMap<>();
        this.percentageModifiers = new HashMap<>();
    }

    /**
     * Изучает талант или повышает его уровень
     */
    public boolean upgrade() {
        if (tier < maxTier && canUpgrade()) {
            tier++;
            if (tier == 1) {
                isActive = true;
                onActivate();
            }
            onUpgrade();
            return true;
        }
        return false;
    }

    /**
     * Проверяет, можно ли изучить/улучшить талант
     */
    public boolean canUpgrade() {
        if (owner == null) return false;

        // Проверка уровня персонажа
        if (owner.getLevel() < requiredLevel) return false;

        // Проверка навыка
        // Здесь нужно интегрироваться с SkillService
        // if (requiredSkill != null && !skillService.hasRequiredSkillLevel(owner.getId(), requiredSkill, requiredSkillLevel)) {
        //     return false;
        // }

        // Проверка предварительного таланта
        if (prerequisiteTalent != null && !owner.hasTalent(prerequisiteTalent)) {
            return false;
        }

        return true;
    }

    /**
     * Применяет модификаторы статов к персонажу
     */
    public void applyStatModifiers(Map<String, Integer> playerStats) {
        if (!isActive) return;

        // Применение абсолютных модификаторов
        for (Map.Entry<String, Integer> modifier : statModifiers.entrySet()) {
            String statName = modifier.getKey();
            int bonusPerTier = modifier.getValue();
            int totalBonus = bonusPerTier * tier;

            if (playerStats.containsKey(statName)) {
                playerStats.put(statName, playerStats.get(statName) + totalBonus);
            }
        }

        // Применение процентных модификаторов
        for (Map.Entry<String, Double> modifier : percentageModifiers.entrySet()) {
            String statName = modifier.getKey();
            double bonusPercentPerTier = modifier.getValue();
            double totalBonusPercent = bonusPercentPerTier * tier;

            if (playerStats.containsKey(statName)) {
                int currentValue = playerStats.get(statName);
                int bonusValue = (int) (currentValue * totalBonusPercent / 100.0);
                playerStats.put(statName, currentValue + bonusValue);
            }
        }
    }

    /**
     * Добавляет модификатор стата
     */
    public void addStatModifier(String statName, int bonusPerTier) {
        statModifiers.put(statName, bonusPerTier);
    }

    /**
     * Добавляет процентный модификатор стата
     */
    public void addPercentageModifier(String statName, double percentPerTier) {
        percentageModifiers.put(statName, percentPerTier);
    }

    /**
     * Вызывается при первом изучении таланта
     */
    protected void onActivate() {
        if (specialEffect != null) {
            specialEffect.onActivate(owner);
        }
    }

    /**
     * Вызывается при каждом улучшении таланта
     */
    protected void onUpgrade() {
        if (specialEffect != null) {
            specialEffect.onUpgrade(owner, tier);
        }
    }

    /**
     * Получает описание с текущими бонусами
     */
    public String getDetailedDescription() {
        StringBuilder sb = new StringBuilder(description);
        sb.append("\n\nТекущий уровень: ").append(tier).append("/").append(maxTier);

        if (!statModifiers.isEmpty()) {
            sb.append("\nБонусы к статам:");
            for (Map.Entry<String, Integer> modifier : statModifiers.entrySet()) {
                int totalBonus = modifier.getValue() * tier;
                sb.append("\n  ").append(modifier.getKey()).append(": +").append(totalBonus);
            }
        }

        if (!percentageModifiers.isEmpty()) {
            sb.append("\nПроцентные бонусы:");
            for (Map.Entry<String, Double> modifier : percentageModifiers.entrySet()) {
                double totalBonus = modifier.getValue() * tier;
                sb.append("\n  ").append(modifier.getKey()).append(": +").append(totalBonus).append("%");
            }
        }

        return sb.toString();
    }
}