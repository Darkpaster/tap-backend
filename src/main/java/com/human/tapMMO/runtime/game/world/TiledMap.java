package com.human.tapMMO.runtime.game.world;

import com.human.tapMMO.model.game.world.TiledTileset;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TiledMap {
    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;
    private List<TiledTileset> tilesets = new ArrayList<>();
    private List<TiledLayer> layers = new ArrayList<>();
    private List<TiledObjectLayer> objectLayers = new ArrayList<>();

    public TiledMap(int width, int height, int tileWidth, int tileHeight) {
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public void addTileset(TiledTileset tileset) {
        tilesets.add(tileset);
    }

    public void addLayer(TiledLayer layer) {
        layers.add(layer);
    }

    public void addObjectLayer(TiledObjectLayer objectLayer) {
        objectLayers.add(objectLayer);
    }

    /**
     * Получение информации о проходимости для позиции
     * @param x координата x
     * @param y координата y
     * @return true если позиция проходима, false в противном случае
     */
    public boolean isWalkable(int x, int y) {
        // Проверка, находится ли точка в пределах карты
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        // Проверка всех слоев коллизий
        for (TiledLayer layer : layers) {
            if (layer.getName().contains("collision")) {
                int tileId = layer.getTileAt(x, y);
                if (tileId != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Получение объектов определенного типа
     * @param type тип объекта
     * @return список объектов заданного типа
     */
    public List<TiledObject> getObjectsByType(String type) {
        List<TiledObject> result = new ArrayList<>();

        for (TiledObjectLayer layer : objectLayers) {
            for (TiledObject object : layer.getObjects()) {
                if (object.getType().equals(type)) {
                    result.add(object);
                }
            }
        }

        return result;
    }
}
