package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "talents")
public class Talent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "namr", updatable = false, nullable = false)
    private String name;
//    private int level;
//    private int maxLevel;

//    @Enumerated(EnumType.STRING)
//    private TalentType type; // ACTIVE, PASSIVE

//    @Enumerated(EnumType.STRING)
//    private TalentCategory category; // COMBAT, NON_COMBAT

//    @Enumerated(EnumType.STRING)
//    private TalentSubCategory subCategory; // BODY_CONTROL, RATIONALITY, MAGIC, etc.

//    @Enumerated(EnumType.STRING)
//    private TalentSpecialization specialization; // TRADING, PSYCHOLOGY, POLITICS, etc.
}
