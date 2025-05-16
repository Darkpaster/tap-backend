package com.human.tapMMO.runtime.game.world;

/**
 * Represents a frame in an animation sequence
 */
public class AnimationFrame {
    public final int tileId;
    public final int duration; // in milliseconds

    public AnimationFrame(int tileId, int duration) {
        this.tileId = tileId;
        this.duration = duration;
    }
}
