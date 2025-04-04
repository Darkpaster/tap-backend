package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "character_stats")
public class CharacterStats {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "character_id", updatable = false, nullable = false, unique = true)
    private Long characterId;

    @Column(name = "health", nullable = false)
    private int health = 100;

    @Column(name = "mana", nullable = false)
    private int mana = 0;

    @Column(name = "stamina", nullable = false)
    private int stamina = 20;

    @Column(name = "strength", nullable = false)
    private int strength = 1;
    @Column(name = "agility", nullable = false)
    private int agility = 1;
    @Column(name = "intellect", nullable = false)
    private int intellect = 1;
}
