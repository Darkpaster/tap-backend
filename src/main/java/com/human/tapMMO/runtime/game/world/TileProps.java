package com.human.tapMMO.runtime.game.world;

/**
 * Properties of a tile
 */
public class TileProps {
    public boolean isWalkable;
    public boolean renderAfter;
    public int damage;
    public boolean animated;

    public TileProps(boolean isWalkable, boolean renderAfter, int damage, boolean animated) {
        this.isWalkable = isWalkable;
        this.renderAfter = renderAfter;
        this.damage = damage;
        this.animated = animated;
    }
}
