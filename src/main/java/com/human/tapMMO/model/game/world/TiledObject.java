package com.human.tapMMO.model.game.world;

import lombok.Getter;

import java.util.Map;

@Getter
class TiledObject {
    private int id;
    private String name;
    private String type;
    private float x;
    private float y;
    private float width;
    private float height;
    private Map<String, Object> properties;

    public TiledObject(int id, String name, String type, float x, float y,
                       float width, float height, Map<String, Object> properties) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.properties = properties;
    }

    /**
     * Получение значения свойства объекта
     * @param name имя свойства
     * @param defaultValue значение по умолчанию
     * @return значение свойства или значение по умолчанию
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String name, T defaultValue) {
        Object value = properties.get(name);
        if (value != null && defaultValue.getClass().isInstance(value)) {
            return (T) value;
        }
        return defaultValue;
    }
}
