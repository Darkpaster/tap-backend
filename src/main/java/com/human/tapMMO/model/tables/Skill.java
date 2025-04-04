package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "skills")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "character_id", updatable = false, nullable = false)
    private Long characterId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "level", nullable = false)
    private int level = 1;

    @Column(name = "experience", nullable = false)
    private int experience = 0;
}
