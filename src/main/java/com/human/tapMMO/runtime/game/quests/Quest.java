package com.human.tapMMO.runtime.game.quests;

import com.human.tapMMO.runtime.game.quests.requirement.QuestRequirement;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class Quest {
    private String id;
    private String title;
    private String description;
    private QuestNode rootNode;
    private Map<String, Reward> rewards;
    private List<QuestRequirement> requirements;
    private QuestStatus status;

    public Quest(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.rewards = new HashMap<>();
        this.requirements = new ArrayList<>();
        this.status = QuestStatus.NOT_STARTED;
    }
}