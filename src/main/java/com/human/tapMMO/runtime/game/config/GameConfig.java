package com.human.tapMMO.runtime.game.config;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

/**
 * Game configuration settings loaded from application.properties
 */
public abstract class GameConfig {
    public static final int TILE_SIZE = 16;

    private double defaultTileScale;
}