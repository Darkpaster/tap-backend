package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mobs")
public class Mob { //запрос на сохранение в бд редко, частые обновления по вебсокету (ActorState)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "x", nullable = false)
    private int x = 0;
    @Column(name = "y", nullable = false)
    private int y = 0;

    @Column(name = "health", nullable = false)
    private int health;

    @Column(name = "respawn_time", nullable = false)
    private int respawnTime; // на сервере нужен колбэк с таймером на респавн

    @Column(name = "is_alive", nullable = false)
    private boolean isAlive = true;
}
