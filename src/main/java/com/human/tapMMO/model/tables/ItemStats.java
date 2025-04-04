package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "item_stats")
public class ItemStats {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "item_id", updatable = false, nullable = false, unique = true)
    private Long itemId;

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

    @Column(name = "damage", nullable = false)
    private int damage;

    @Column(name = "magic_damage", nullable = false)
    private int magicDamage;

    @Column(name = "armor", nullable = false)
    private int armor;
    @Column(name = "magic_armor", nullable = false)
    private int magicArmor;

    @Column(name = "critical_damage", nullable = false)
    private byte criticalDamage;

    @Column(name = "critical_chance", nullable = false)
    private float criticalChance;

    @Column(name = "health_regeneration", nullable = false)
    private float healthRegeneration;

    @Column(name = "mana_regeneration", nullable = false)
    private float manaRegeneration;

    @Column(name = "stamina_regeneration", nullable = false)
    private float staminaRegeneration;


}
