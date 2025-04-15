package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "guilds")
public class Guild {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "level", nullable = false)
    private Integer level = 1;

    @Column(name = "experience", nullable = false)
    private Integer experience = 0;

    @Column(name = "max_members", nullable = false)
    private Integer maxMembers = 50;

//    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<Player> members = new HashSet<>();
//
//    @OneToOne
//    @JoinColumn(name = "master_id")
//    private Player master;
//
//    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<GuildAchievement> achievements = new HashSet<>();
//
//    @Column(name = "guild_crest_url")
//    private String guildCrestUrl;
//
//    @Column(name = "motd")
//    private String messageOfTheDay;
//
//    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<GuildBank> bankItems = new HashSet<>();
}
