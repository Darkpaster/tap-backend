package com.human.tapMMO.runtime.game.quest;

import lombok.Getter;

@Getter
public public class PlayerContext {
    // Геттеры и сеттеры
    private String playerId;
    private Map<String, Integer> stats;
    private Set<String> completedQuests;
    private Map<String, Integer> inventory;

    public PlayerContext(String playerId) {
        this.playerId = playerId;
        this.stats = new HashMap<>();
        this.completedQuests = new HashSet<>();
        this.inventory = new HashMap<>();
    }

    public void setStat(String statName, int value) { stats.put(statName, value); }
    public int getStat(String statName) { return stats.getOrDefault(statName, 0); }

    public void completeQuest(String questId) { completedQuests.add(questId); }
    public boolean hasCompletedQuest(String questId) { return completedQuests.contains(questId); }

    public void addItem(String itemId, int amount) {
        inventory.put(itemId, inventory.getOrDefault(itemId, 0) + amount);
    }
    public boolean hasItem(String itemId, int amount) {
        return inventory.getOrDefault(itemId, 0) >= amount;
    }
}
