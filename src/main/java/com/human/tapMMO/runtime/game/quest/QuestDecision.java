package com.human.tapMMO.runtime.game.quest;

import lombok.Getter;

@Getter
public class QuestDecision {
    // Геттеры и сеттеры
    private String id;
    private String text;
    private QuestNode nextNode;

    public QuestDecision(String id, String text, QuestNode nextNode) {
        this.id = id;
        this.text = text;
        this.nextNode = nextNode;
    }

}

