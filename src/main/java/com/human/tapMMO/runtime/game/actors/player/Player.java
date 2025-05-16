package com.human.tapMMO.runtime.game.actors.player;

import com.human.tapMMO.runtime.game.actors.Actor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Player extends Actor {
    // Геттеры и сеттеры
    private int experience;
    private int nextLevelExperience;
    private final Map<String, Integer> stats;
    private final List<Item> inventory;
    private final Equipment equipment;
    private int gold;
    private final List<Quest> activeQuests;

    public Player(String name) {
        this.name = name;
        this.experience = 0;
        this.nextLevelExperience = 100; // Опыт для 2-го уровня

        // Инициализация характеристик
        this.stats = new HashMap<>();
        stats.put("strength", 10);
        stats.put("agility", 10);
        stats.put("intelligence", 10);
        stats.put("vitality", 10);

        this.inventory = new ArrayList<>();
        this.equipment = new Equipment();
        this.gold = 0;
        this.activeQuests = new ArrayList<>();
    }

    public void gainExperience(int exp) {
        experience += exp;

        // Проверка на повышение уровня
        while (experience >= nextLevelExperience) {
            levelUp();
        }
    }

    private void levelUp() {
        setLevel(getLevel() + 1);
        experience -= nextLevelExperience;
        nextLevelExperience = calculateNextLevelExperience();

        // Увеличение характеристик
        int healthIncrease = stats.get("vitality") / 2;
        setMaxHealth(getMaxHealth() + healthIncrease);
        setHealth(getMaxHealth());

        // Оповещение о повышении уровня
    }

    private int calculateNextLevelExperience() {
        // Простая формула расчета опыта для следующего уровня
        return 100 * getLevel() * getLevel();
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public boolean removeItem(Item item) {
        return inventory.remove(item);
    }

    public boolean equipItem(Item item) {
        if (!inventory.contains(item) || !(item instanceof EquippableItem equippableItem)) {
            return false;
        }

        equipment.equip(equippableItem);
        return true;
    }

    public void unequipItem(EquipmentSlot slot) {
        EquippableItem item = equipment.unequip(slot);
        if (item != null) {
            inventory.add(item);
        }
    }

    public void attack(Actor target) {
        int damage = calculateDamage();
        target.takeDamage(damage);
    }

    private int calculateDamage() {
        // Расчет базового урона на основе характеристик и экипировки
        int baseDamage = stats.get("strength") + getLevel() * 2;
        return equipment.applyDamageModifiers(baseDamage);
    }

    @Override
    public void onDeath() {
        // Логика смерти игрока (потеря опыта, перемещение на точку возрождения и т.д.)
    }

    public void addQuest(Quest quest) {
        activeQuests.add(quest);
    }

    public boolean completeQuest(Quest quest) {
        if (activeQuests.contains(quest) && quest.isCompleted()) {
            activeQuests.remove(quest);
            gainExperience(quest.getExperienceReward());
            gainGold(quest.getGoldReward());

            // Получение предметных наград
            for (Item item : quest.getItemRewards()) {
                addItem(item);
            }

            return true;
        }
        return false;
    }

    public void gainGold(int amount) {
        gold += amount;
    }

    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    // Внутренние классы для инвентаря и снаряжения
    @Getter
    @Setter
    public static class Item {
        private String name;
        private String description;
        private int value;

        public Item(String name, String description, int value) {
            this.name = name;
            this.description = description;
            this.value = value;
        }

        // Геттеры
        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getValue() {
            return value;
        }
    }

    @Getter
    @Setter
    public static class EquippableItem extends Item {
        private EquipmentSlot slot;
        private Map<String, Integer> statBonuses;

        public EquippableItem(String name, String description, int value, EquipmentSlot slot) {
            super(name, description, value);
            this.slot = slot;
            this.statBonuses = new HashMap<>();
        }

        public void addStatBonus(String stat, int bonus) {
            statBonuses.put(stat, bonus);
        }

    }

    public enum EquipmentSlot {
        HEAD, CHEST, LEGS, FEET, HANDS, WEAPON, OFF_HAND
    }

    @Getter
    public static class Equipment {
        private final Map<EquipmentSlot, EquippableItem> equipped;

        public Equipment() {
            this.equipped = new HashMap<>();
        }

        public void equip(EquippableItem item) {
            equipped.put(item.getSlot(), item);
        }

        public EquippableItem unequip(EquipmentSlot slot) {
            return equipped.remove(slot);
        }

        public int applyDamageModifiers(int baseDamage) {
            int totalDamage = baseDamage;

            // Добавление бонусов от экипировки
            for (EquippableItem item : equipped.values()) {
                if (item.getStatBonuses().containsKey("damage")) {
                    totalDamage += item.getStatBonuses().get("damage");
                }
            }

            return totalDamage;
        }

    }

    @Getter
    @Setter
    public static class Quest {
        private String name;
        private String description;
        private Map<String, Integer> objectives;
        private Map<String, Integer> progress;
        private int experienceReward;
        private int goldReward;
        private List<Item> itemRewards;

        public Quest(String name, String description, int experienceReward, int goldReward) {
            this.name = name;
            this.description = description;
            this.objectives = new HashMap<>();
            this.progress = new HashMap<>();
            this.experienceReward = experienceReward;
            this.goldReward = goldReward;
            this.itemRewards = new ArrayList<>();
        }

        public void addObjective(String objective, int target) {
            objectives.put(objective, target);
            progress.put(objective, 0);
        }

        public void updateProgress(String objective, int amount) {
            if (progress.containsKey(objective)) {
                int currentProgress = progress.get(objective);
                int target = objectives.get(objective);
                progress.put(objective, Math.min(currentProgress + amount, target));
            }
        }

        public boolean isCompleted() {
            for (String objective : objectives.keySet()) {
                if (progress.get(objective) < objectives.get(objective)) {
                    return false;
                }
            }
            return true;
        }

        public void addItemReward(Item item) {
            itemRewards.add(item);
        }

    }
}