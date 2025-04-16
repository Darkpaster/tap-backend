package com.human.tapMMO.runtime.game.quests;

import com.human.tapMMO.runtime.game.quests.requirement.QuestRequirement;
import lombok.Getter;

@Getter
public class QuestNode {
    // Геттеры и сеттеры
    private String id;
    private String description;
    private List<QuestDecision> decisions;
    private List<QuestRequirement> requirements;
    private Map<String, Reward> rewards;
    private QuestNodeType type;

    public QuestNode(String id, String description, QuestNodeType type) {
        this.id = id;
        this.description = description;
        this.decisions = new ArrayList<>();
        this.requirements = new ArrayList<>();
        this.rewards = new HashMap<>();
        this.type = type;
    }

    public void addDecision(QuestDecision decision) { this.decisions.add(decision); }

    public void addRequirement(QuestRequirement requirement) { this.requirements.add(requirement); }

    public void addReward(Reward reward) { this.rewards.put(reward.getId(), reward); }
}

// Тип узла квеста
enum QuestNodeType {
    DIALOGUE, // Диалог с NPC
    BATTLE,   // Сражение с врагами
    PUZZLE,   // Головоломка
    COLLECTION, // Сбор предметов
    DECISION  // Точка принятия решения
}