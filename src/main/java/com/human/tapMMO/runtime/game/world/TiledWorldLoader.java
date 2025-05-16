package com.human.tapMMO.runtime.game.world;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TiledWorldLoader {
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    // Кэш загруженных карт
    private Map<String, TiledMap> loadedMaps = new HashMap<>();

    public TiledWorldLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Загружает карту из Tiled JSON файла
     * @param mapPath путь к файлу карты
     * @return объект карты
     * @throws IOException при ошибке чтения файла
     */
    public TiledMap loadMap(String mapPath) throws IOException {
        // Проверка кэша
        if (loadedMaps.containsKey(mapPath)) {
            return loadedMaps.get(mapPath);
        } //static/world.json

        Resource resource = resourceLoader.getResource("classpath:" + mapPath);
        try (InputStream is = resource.getInputStream()) {
            JsonNode rootNode = objectMapper.readTree(is);
            TiledMap map = parseTiledMap(rootNode);

            // Кэширование загруженной карты
            loadedMaps.put(mapPath, map);

            return map;
        }
    }

    /**
     * Парсит JSON узел в объект карты Tiled
     * @param rootNode корневой узел JSON
     * @return объект карты
     */
    private TiledMap parseTiledMap(JsonNode rootNode) {
        int width = rootNode.get("width").asInt();
        int height = rootNode.get("height").asInt();
        int tileWidth = rootNode.get("tilewidth").asInt();
        int tileHeight = rootNode.get("tileheight").asInt();

        TiledMap map = new TiledMap(width, height, tileWidth, tileHeight);

        // Парсинг набора тайлов
        JsonNode tilesetsNode = rootNode.get("tilesets");
        for (JsonNode tilesetNode : tilesetsNode) {
            int firstGid = tilesetNode.get("firstgid").asInt();
            String source = tilesetNode.has("source") ? tilesetNode.get("source").asText() : null;

            // В полных реализациях здесь бы загружались изображения тайлсетов
            map.addTileset(new TiledTileset(firstGid, source));
        }

        // Парсинг слоев
        JsonNode layersNode = rootNode.get("layers");
        for (JsonNode layerNode : layersNode) {
            String type = layerNode.get("type").asText();
            String name = layerNode.get("name").asText();

            if ("tilelayer".equals(type)) {
                // Слой тайлов
                int[] data = parseLayerData(layerNode.get("data"));
                TiledLayer layer = new TiledLayer(name, width, height, data);
                map.addLayer(layer);
            } else if ("objectgroup".equals(type)) {
                // Слой объектов
                List<TiledObject> objects = parseObjects(layerNode.get("objects"));
                TiledObjectLayer objectLayer = new TiledObjectLayer(name, objects);
                map.addObjectLayer(objectLayer);
            }
        }

        return map;
    }

    /**
     * Парсит данные слоя из JSON
     * @param dataNode узел с данными
     * @return массив индексов тайлов
     */
    private int[] parseLayerData(JsonNode dataNode) {
        int[] data = new int[dataNode.size()];
        for (int i = 0; i < dataNode.size(); i++) {
            data[i] = dataNode.get(i).asInt();
        }
        return data;
    }

    /**
     * Парсит объекты из JSON
     * @param objectsNode узел с объектами
     * @return список объектов
     */
    private List<TiledObject> parseObjects(JsonNode objectsNode) {
        List<TiledObject> objects = new ArrayList<>();

        for (JsonNode objectNode : objectsNode) {
            int id = objectNode.get("id").asInt();
            String name = objectNode.has("name") ? objectNode.get("name").asText() : "";
            String type = objectNode.has("type") ? objectNode.get("type").asText() : "";
            float x = objectNode.get("x").floatValue();
            float y = objectNode.get("y").floatValue();
            float width = objectNode.has("width") ? objectNode.get("width").floatValue() : 0;
            float height = objectNode.has("height") ? objectNode.get("height").floatValue() : 0;

            // Парсинг свойств объекта
            Map<String, Object> properties = new HashMap<>();
            if (objectNode.has("properties")) {
                JsonNode propsNode = objectNode.get("properties");
                for (JsonNode propNode : propsNode) {
                    String propName = propNode.get("name").asText();
                    String propType = propNode.get("type").asText();
                    JsonNode valueNode = propNode.get("value");

                    switch (propType) {
                        case "string":
                            properties.put(propName, valueNode.asText());
                            break;
                        case "int":
                            properties.put(propName, valueNode.asInt());
                            break;
                        case "float":
                            properties.put(propName, valueNode.floatValue());
                            break;
                        case "bool":
                            properties.put(propName, valueNode.asBoolean());
                            break;
                    }
                }
            }

            TiledObject object = new TiledObject(id, name, type, x, y, width, height, properties);
            objects.add(object);
        }

        return objects;
    }
}

