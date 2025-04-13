package com.human.tapMMO.model.game.world;

import lombok.Getter;

@Getter
class TiledLayer {
    private String name;
    private int width;
    private int height;
    private int[] data;

    public TiledLayer(String name, int width, int height, int[] data) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.data = data;
    }

    /**
     * Получение индекса тайла в позиции (x, y)
     * @param x координата x
     * @param y координата y
     * @return индекс тайла или 0, если координаты за пределами слоя
     */
    public int getTileAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return 0;
        }
        return data[y * width + x];
    }
}
