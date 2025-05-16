package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "account_preferences")
public class AccountPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "show_fps", nullable = false)
    private boolean showFPS = false;

    @Column(name = "max_fps", nullable = false)
    private byte maxFPS = 30;

    @Column(name = "music_volume", nullable = false)
    private byte musicVolume = 100;

    @Column(name = "sound_volume", nullable = false)
    private byte soundVolume = 100;

    @Column(name = "language", nullable = false)
    private String language = "en";

}
