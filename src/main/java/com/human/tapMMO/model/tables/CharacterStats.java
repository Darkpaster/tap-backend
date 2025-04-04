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
    private int health;

    @Column(name = "mana", nullable = false)
    private int mana;

    @Column(name = "stamina", nullable = false)
    private int stamina;

    @Column(name = "strength", nullable = false)
    private int strength;
    @Column(name = "agility", nullable = false)
    private int agility;
    @Column(name = "intellect", nullable = false)
    private int intellect;
}
