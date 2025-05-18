package com.human.tapMMO.runtime.game.world;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.human.tapMMO.util.BufferedImageBase64Serializer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single tile in the game world
 */
public class Tile {
    public final String name;
    public final TileProps props;

    @JsonSerialize(using = BufferedImageBase64Serializer.class)
    public final BufferedImage image;

    private List<AnimationFrame> animationFrames;

    public Tile(String name, TileProps props, BufferedImage image) {
        this.name = name;
        this.props = props;
        this.image = image;
        this.animationFrames = new ArrayList<>();
    }

    public void addAnimationFrame(AnimationFrame frame) {
        this.animationFrames.add(frame);
    }

    public List<AnimationFrame> getAnimationFrames() {
        return animationFrames;
    }

    public boolean isAnimated() {
        return props.animated && !animationFrames.isEmpty();
    }

    @Override
    public String toString() {
        return "Tile{name='" + name + "', walkable=" + props.isWalkable + "}";
    }
}

