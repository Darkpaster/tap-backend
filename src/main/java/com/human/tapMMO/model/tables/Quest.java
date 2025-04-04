package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "quests")
public class Quest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "character_id", updatable = false, nullable = false)
    private Long characterId;

    @Column(name = "quest", updatable = false, nullable = false)
    private String quest;

    @Column(name = "quest_stage", nullable = false)
    private int questStage;
}
