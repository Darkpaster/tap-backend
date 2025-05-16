package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "guild_logs")
public class GuildLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "guild_id", nullable = false)
    private Guild guild;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "details")
    private String details;

    @ManyToOne
    @JoinColumn(name = "actor_id")
    private Character actor;

    @ManyToOne
    @JoinColumn(name = "target_id")
    private Character target;
}
