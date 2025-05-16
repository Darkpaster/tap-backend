package com.human.tapMMO.runtime.game.world;

import java.util.List;

class TiledObjectLayer {
    private String name;
    private List<TiledObject> objects;

    public TiledObjectLayer(String name, List<TiledObject> objects) {
        this.name = name;
        this.objects = objects;
    }

    // Геттеры
    public String getName() {
        return name;
    }

    public List<TiledObject> getObjects() {
        return objects;
    }
}
