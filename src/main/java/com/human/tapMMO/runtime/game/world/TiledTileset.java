package com.human.tapMMO.runtime.game.world;

import lombok.Getter;

@Getter
public class TiledTileset {
    private int firstGid;
    private String source;

    public TiledTileset(int firstGid, String source) {
        this.firstGid = firstGid;
        this.source = source;
    }
}