package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "characters")
public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "account_id", updatable = false, nullable = false)
    private Long accountId;

    @Column(name = "x", nullable = false)
    private int x;
    @Column(name = "y", nullable = false)
    private int y;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "level", nullable = false)
    private int level = 1;

    @Column(name = "experience", nullable = false)
    private Long experience = 0L;

    @Column(name = "gold", nullable = false)
    private int gold = 0;

    @Column(name = "sanity", nullable = false)
    private int sanity = 0;

    private int reputation = 0;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(name = "last_login", nullable = false)
    private LocalDateTime lastLogin = LocalDateTime.now();
}
