package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "buffs")
public class Buff {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "buff_type", updatable = false, nullable = false)
    private String buffType;

    @Column(name = "seconds_left", nullable = false)
    private int secondsLeft;
}