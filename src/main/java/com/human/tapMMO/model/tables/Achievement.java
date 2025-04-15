package com.human.tapMMO.model.tables;

import com.human.tapMMO.model.game.AchievementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "achievements")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "account_id", updatable = false, nullable = false)
    private long accountId;

    @Column(name = "name", updatable = false, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "achievement_type", updatable = false, nullable = false)
    private AchievementType type;
}
