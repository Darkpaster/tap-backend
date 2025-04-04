package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "group_leader_id", nullable = false)
    private Long groupLeaderId;

    @Column(name = "creation_time", nullable = false, updatable = false)
    private LocalDateTime creationTime = LocalDateTime.now();
}
