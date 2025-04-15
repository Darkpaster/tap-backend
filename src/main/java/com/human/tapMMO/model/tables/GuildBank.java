package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "guild_banks")
public class GuildBank {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "guild_id", nullable = false)
    private Guild guildId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "tab_id", nullable = false)
    private Integer tabId;

    @Column(name = "slot_id", nullable = false)
    private Integer slotId;
}