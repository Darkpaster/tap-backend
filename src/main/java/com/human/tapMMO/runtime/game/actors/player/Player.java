package com.human.tapMMO.runtime.game.actors.player;

import com.human.tapMMO.runtime.game.actors.Actor;
import com.human.tapMMO.runtime.game.buffs.Buff;
import com.human.tapMMO.runtime.game.talents.RuntimeTalent;
import com.human.tapMMO.runtime.game.abilities.PassiveAbility;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
public class Player extends Actor {
    // Базовые характеристики
    private int experience;
    private final Map<String, Integer> baseStats; // Базовые статы без модификаторов
    private final Map<String, Integer> stats; // Финальные статы с модификаторами
    private final List<Item> inventory;
    private final Equipment equipment;
    private long gold;
    private int mana;
    private int maxMana;
    private int stamina;
    private int maxStamina;
    private final List<Quest> activeQuests;

    // Новые системы
    private final List<Buff> activeBuffs;
    private final List<PassiveAbility> passiveAbilities;
    private final List<RuntimeTalent> talents;

    @Setter
    private UUID sessionId;

    public Player() {
        super();
        this.experience = 0;

        // Инициализация базовых характеристик
        this.baseStats = new HashMap<>();
        this.stats = new HashMap<>();
        baseStats.put("strength", 10);
        baseStats.put("agility", 10);
        baseStats.put("intelligence", 10);
        baseStats.put("vitality", 10);

        this.inventory = new ArrayList<>();
        this.equipment = new Equipment();
        this.gold = 0L;
        this.activeQuests = new ArrayList<>();

        // Инициализация новых систем
        this.activeBuffs = new ArrayList<>();
        this.passiveAbilities = new ArrayList<>();
        this.talents = new ArrayList<>();

        // Первоначальный расчет статов
        recalculateStats();
    }

    public void gainExperience(int exp) {
        experience += exp;

        // Проверка на повышение уровня
        while (experience >= level * level + 4) {
            levelUp();
        }
    }

    private void levelUp() {
        experience -= level * level + 4;
        setLevel(getLevel() + 1);

        // Увеличение базовых характеристик
        int healthIncrease = baseStats.get("vitality") / 2;
        setMaxHealth(getMaxHealth() + healthIncrease);
        setHealth(getMaxHealth());

        // Перерасчет всех статов после повышения уровня
        recalculateStats();
    }

    /**
     * Перерасчитывает все статы игрока на основе базовых характеристик,
     * экипировки, баффов, талантов и пассивных способностей
     */
    public void recalculateStats() {
        // Копируем базовые статы
        stats.clear();
        for (Map.Entry<String, Integer> entry : baseStats.entrySet()) {
            stats.put(entry.getKey(), entry.getValue());
        }

        // Применяем бонусы от экипировки
        applyEquipmentBonuses();

        // Применяем бонусы от талантов
        applyTalentBonuses();

        // Применяем бонусы от пассивных способностей
        applyPassiveAbilityBonuses();

        // Применяем бонусы от баффов (баффы применяются последними)
        applyBuffBonuses();

        // Обновляем производные характеристики
        updateDerivedStats();
    }

    private void applyEquipmentBonuses() {
        for (EquippableItem item : equipment.getEquipped().values()) {
            for (Map.Entry<String, Integer> bonus : item.getStatBonuses().entrySet()) {
                String statName = bonus.getKey();
                int bonusValue = bonus.getValue();

                if (stats.containsKey(statName)) {
                    stats.put(statName, stats.get(statName) + bonusValue);
                }
            }
        }
    }

    private void applyTalentBonuses() {
        for (RuntimeTalent talent : talents) {
            if (talent.isActive()) {
                talent.applyStatModifiers(stats);
            }
        }
    }

    private void applyPassiveAbilityBonuses() {
        for (PassiveAbility ability : passiveAbilities) {
            if (ability.isActive()) {
                ability.applyStatModifiers(stats);
            }
        }
    }

    private void applyBuffBonuses() {
        // Баффы обрабатываются через BuffService, но здесь мы можем
        // получить их текущие модификаторы для пересчета
        for (Buff buff : activeBuffs) {
            if (buff.isPositive()) {
                // Применение бонусов от баффов
                // Конкретная логика зависит от типа баффа
            }
        }
    }

    private void updateDerivedStats() {
        // Обновление здоровья на основе жизненной силы
        int vitalityBonus = (stats.get("vitality") - baseStats.get("vitality")) * 5;
        setMaxHealth(getMaxHealth() + vitalityBonus);

        // Обновление маны на основе интеллекта
        int intelligenceBonus = (stats.get("intelligence") - baseStats.get("intelligence")) * 3;
        maxMana = 50 + intelligenceBonus;

        // Обновление выносливости на основе ловкости
        int agilityBonus = (stats.get("agility") - baseStats.get("agility")) * 2;
        maxStamina = 100 + agilityBonus;
    }

    // Методы для работы с баффами
    public void addBuff(Buff buff) {
        activeBuffs.add(buff);
        recalculateStats();
    }

    public void removeBuff(Buff buff) {
        activeBuffs.remove(buff);
        recalculateStats();
    }

    public boolean hasBuff(Class<? extends Buff> buffClass) {
        return activeBuffs.stream().anyMatch(buffClass::isInstance);
    }

    // Методы для работы с пассивными способностями
    public void addPassiveAbility(PassiveAbility ability) {
        passiveAbilities.add(ability);
        ability.setOwner(this);
        recalculateStats();
    }

    public void removePassiveAbility(PassiveAbility ability) {
        passiveAbilities.remove(ability);
        recalculateStats();
    }

    public boolean hasPassiveAbility(String abilityName) {
        return passiveAbilities.stream()
                .anyMatch(ability -> ability.getName().equals(abilityName));
    }

    // Методы для работы с талантами
    public void addTalent(RuntimeTalent talent) {
        talents.add(talent);
        talent.setOwner(this);
        recalculateStats();
    }

    public void removeTalent(RuntimeTalent talent) {
        talents.remove(talent);
        recalculateStats();
    }

    public boolean hasTalent(String talentName) {
        return talents.stream()
                .anyMatch(talent -> talent.getName().equals(talentName));
    }

    public RuntimeTalent getTalent(String talentName) {
        return talents.stream()
                .filter(talent -> talent.getName().equals(talentName))
                .findFirst()
                .orElse(null);
    }

    // Методы для получения модифицированных статов
    public int getFinalStat(String statName) {
        return stats.getOrDefault(statName, 0);
    }

    public int getBaseStat(String statName) {
        return baseStats.getOrDefault(statName, 0);
    }

    public void setBaseStat(String statName, int value) {
        baseStats.put(statName, value);
        recalculateStats();
    }

    // Обновленные методы с учетом новых систем
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
        recalculateStats(); // Пересчитываем статы после экипировки
        return true;
    }

    public void unequipItem(EquipmentSlot slot) {
        EquippableItem item = equipment.unequip(slot);
        if (item != null) {
            inventory.add(item);
            recalculateStats(); // Пересчитываем статы после снятия экипировки
        }
    }

    private int calculateDamage() {
        // Расчет урона на основе финальных характеристик
        int baseDamage = getFinalStat("strength") + getLevel() * 2;
        return equipment.applyDamageModifiers(baseDamage);
    }

    @Override
    public void onDeath() {
        // Очистка временных эффектов при смерти
        activeBuffs.clear();

        // Деактивация пассивных способностей
        for (PassiveAbility ability : passiveAbilities) {
            ability.onOwnerDeath();
        }

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

    // Остальные внутренние классы остаются без изменений...
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