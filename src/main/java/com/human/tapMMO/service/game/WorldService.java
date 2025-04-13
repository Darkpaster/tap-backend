package com.human.tapMMO.service.game;

import com.human.tapMMO.model.game.world.TiledWorldLoader;
import com.human.tapMMO.runtime.game.actor.Actor;
import com.human.tapMMO.runtime.game.actor.player.Player;
import com.human.tapMMO.runtime.game.world.TiledMap;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WorldService {
    private final TiledWorldLoader worldLoader;

    // Карты мира
    private Map<String, TiledMap> maps = new HashMap<>();

    // Активные сущности на каждой карте
    private Map<String, List<Actor>> entitiesByMap = new ConcurrentHashMap<>();

    // Сопоставление игроков с текущими картами
    private Map<Player, String> playerMaps = new ConcurrentHashMap<>();

    // Коллизия и поиск путей
    private Map<String, CollisionGrid> collisionGrids = new HashMap<>();

    public WorldService(TiledWorldLoader worldLoader) {
        this.worldLoader = worldLoader;
    }

    @PostConstruct
    public void init() {
        // Загрузка всех карт при запуске
        try {
            loadMap("maps/town.json");
            loadMap("maps/dungeon.json");
            loadMap("maps/forest.json");
            // Другие карты...
        } catch (IOException e) {
            throw new RuntimeException("Failed to load game maps", e);
        }
    }

    /**
     * Загружает карту и инициализирует её
     * @param mapPath путь к файлу карты
     * @throws IOException при ошибке чтения файла
     */
    private void loadMap(String mapPath) throws IOException {
        TiledMap map = worldLoader.loadMap(mapPath);
        String mapName = mapPath.substring(mapPath.lastIndexOf('/') + 1, mapPath.lastIndexOf('.'));

        maps.put(mapName, map);
        entitiesByMap.put(mapName, new ArrayList<>());

        // Создание сетки коллизий для карты
        collisionGrids.put(mapName, createCollisionGrid(map));

        // Инициализация сущностей карты (мобов, NPC и т.д.)
        initializeMapEntities(mapName, map);
    }

    /**
     * Создает сетку коллизий для карты
     * @param map карта
     * @return сетка коллизий
     */
    private CollisionGrid createCollisionGrid(TiledMap map) {
        boolean[][] grid = new boolean[map.getWidth()][map.getHeight()];

        // Инициализация сетки (по умолчанию всё проходимо)
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                grid[x][y] = true;
            }
        }

        // Обновление сетки на основе слоев коллизий
        for (TiledLayer layer : map.getLayers()) {
            if (layer.getName().contains("collision")) {
                for (int x = 0; x < map.getWidth(); x++) {
                    for (int y = 0; y < map.getHeight(); y++) {
                        if (layer.getTileAt(x, y) != 0) {
                            grid[x][y] = false;
                        }
                    }
                }
            }
        }

        return new CollisionGrid(grid);
    }

    /**
     * Инициализирует сущности на карте на основе объектов Tiled
     * @param mapName имя карты
     * @param map карта
     */
    private void initializeMapEntities(String mapName, TiledMap map) {
        List<Actor> mapEntities = entitiesByMap.get(mapName);

        // Спавн мобов
        for (TiledObject obj : map.getObjectsByType("mob_spawn")) {
            String mobType = obj.getProperty("mobType", "");
            int level = obj.getProperty("level", 1);

            // В реальной реализации тут была бы фабрика для создания мобов
            // Mob mob = mobFactory.createMob(mobType, level);
            // mob.setPosition(new Position(obj.getX() / map.getTileWidth(), obj.getY() / map.getTileHeight(), 0));
            // mapEntities.add(mob);
        }

        // Спавн NPC
        for (TiledObject obj : map.getObjectsByType("npc")) {
            String npcName = obj.getProperty("name", "Unknown NPC");
            // Аналогично с NPC
        }
    }

    /**
     * Обновляет игровой мир
     * @param deltaTime прошедшее время в секундах
     */
    public void update(float deltaTime) {
        // Обновление всех карт
        for (String mapName : entitiesByMap.keySet()) {
            updateMap(mapName, deltaTime);
        }
    }

    /**
     * Обновляет отдельную карту
     * @param mapName имя карты
     * @param deltaTime прошедшее время в секундах
     */
    private void updateMap(String mapName, float deltaTime) {
        List<Actor> entities = entitiesByMap.get(mapName);

        // Обновление всех сущностей на карте
        for (Actor entity : new ArrayList<>(entities)) {
            if (entity instanceof Mob) {
                ((Mob) entity).updateAI();
            }

            // Другие обновления сущностей
        }
    }

/**
 * Добавляет игрока на карту
 * @param player игрок
 * @param mapName имя карты
 * @param x координата x
 * @param y координата y
 **/
public void addPlayer(Player player, String mapName, float x, float y) {
    if (!maps.containsKey(mapName)) {
        throw new IllegalArgumentException("Map not found: " + mapName);
    }

    // Удаление игрока с предыдущей карты
    String currentMap = playerMaps.get(player);
    if (currentMap != null) {
        entitiesByMap.get(currentMap).remove(player);
    }

    // Установка позиции игрока
    player.getPosition().setX(x);
    player.getPosition().setY(y);

    // Добавление игрока на новую карту
    entitiesByMap.get(mapName).add(player);
    playerMaps.put(player, mapName);

    // Отправка информации о новой карте игроку (в реальной имплементации)
    // packetSender.sendMapData(player, mapName, maps.get(mapName));
}

    /**
     * Перемещает игрока на другую карту
     * @param player игрок
     * @param targetMapName имя целевой карты
     * @param targetX целевая координата x
     * @param targetY целевая координата y
     */
    public void changePlayerMap(Player player, String targetMapName, float targetX, float targetY) {
        addPlayer(player, targetMapName, targetX, targetY);

        // Загрузка видимых сущностей для игрока
        loadVisibleEntities(player);
    }

    /**
     * Загружает видимые сущности для игрока
     * @param player игрок
     */
    private void loadVisibleEntities(Player player) {
        String mapName = playerMaps.get(player);
        if (mapName == null) return;

        List<Actor> entities = entitiesByMap.get(mapName);
        List<Actor> visibleEntities = new ArrayList<>();

        // Фильтрация сущностей в радиусе видимости
        float viewDistance = 20.0f; // В тайлах

        for (Actor entity : entities) {
            if (entity == player) continue;

            float distance = calculateDistance(player.getPosition(), entity.getPosition());
            if (distance <= viewDistance) {
                visibleEntities.add(entity);
            }
        }

        // Отправка видимых сущностей игроку (в реальной имплементации)
        // packetSender.sendVisibleEntities(player, visibleEntities);
    }

    /**
     * Вычисляет расстояние между двумя позициями
     */
    private float calculateDistance(Position pos1, Position pos2) {
        float dx = pos1.getX() - pos2.getX();
        float dy = pos1.getY() - pos2.getY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Проверяет, проходима ли позиция на карте
     * @param mapName имя карты
     * @param x координата x
     * @param y координата y
     * @return true если позиция проходима
     */
    public boolean isWalkable(String mapName, int x, int y) {
        if (!maps.containsKey(mapName)) {
            return false;
        }

        CollisionGrid grid = collisionGrids.get(mapName);
        return grid.isWalkable(x, y);
    }

    /**
     * Находит путь между двумя точками на карте
     * @param mapName имя карты
     * @param startX начальная координата x
     * @param startY начальная координата y
     * @param endX конечная координата x
     * @param endY конечная координата y
     * @return список позиций, составляющих путь, или пустой список если путь не найден
     */
    public List<Position> findPath(String mapName, int startX, int startY, int endX, int endY) {
        if (!maps.containsKey(mapName)) {
            return new ArrayList<>();
        }

        CollisionGrid grid = collisionGrids.get(mapName);
        return grid.findPath(startX, startY, endX, endY);
    }

    /**
     * Получает всех игроков на карте
     * @param mapName имя карты
     * @return список игроков
     */
    public List<Player> getPlayersOnMap(String mapName) {
        if (!entitiesByMap.containsKey(mapName)) {
            return new ArrayList<>();
        }

        List<Player> players = new ArrayList<>();
        for (Actor entity : entitiesByMap.get(mapName)) {
            if (entity instanceof Player) {
                players.add((Player) entity);
            }
        }

        return players;
    }

    /**
     * Получает всех мобов на карте
     * @param mapName имя карты
     * @return список мобов
     */
    public List<Mob> getMobsOnMap(String mapName) {
        if (!entitiesByMap.containsKey(mapName)) {
            return new ArrayList<>();
        }

        List<Mob> mobs = new ArrayList<>();
        for (Actor entity : entitiesByMap.get(mapName)) {
            if (entity instanceof Mob) {
                mobs.add((Mob) entity);
            }
        }

        return mobs;
    }

    /**
     * Получает карту, на которой находится игрок
     * @param player игрок
     * @return имя карты или null, если игрок не находится ни на одной карте
     */
    public String getPlayerMap(Player player) {
        return playerMaps.get(player);
    }

    /**
     * Получает все сущности в определенном радиусе от позиции
     * @param mapName имя карты
     * @param centerX центральная координата x
     * @param centerY центральная координата y
     * @param radius радиус
     * @return список сущностей в радиусе
     */
    public List<Actor> getEntitiesInRadius(String mapName, float centerX, float centerY, float radius) {
        if (!entitiesByMap.containsKey(mapName)) {
            return new ArrayList<>();
        }

        List<Actor> result = new ArrayList<>();
        Position center = new Position(centerX, centerY, 0);

        for (Actor entity : entitiesByMap.get(mapName)) {
            float distance = calculateDistance(center, entity.getPosition());
            if (distance <= radius) {
                result.add(entity);
            }
        }

        return result;
    }
}