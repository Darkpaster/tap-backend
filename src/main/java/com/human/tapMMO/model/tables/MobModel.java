package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "mobs")
public class MobModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "x", nullable = false)
    private float x = 0;
    @Column(name = "y", nullable = false)
    private float y = 0;

    @Column(name = "health", nullable = false)
    private int health; //обновлять при респавне

    @Column(name = "respawn_time", nullable = false)
    private Instant respawnTime = Instant.now().plusSeconds(10);

    @Column(name = "state", nullable = false)
    @Pattern(regexp = "dead|alive")
    private String state = "alive";

    @Column(name = "name", nullable = false, updatable = false)
    private String name;
}
