package com.human.tapMMO.runtime.game.world;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.human.tapMMO.runtime.game.config.GameConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class MapManager {
    @Autowired
    private ResourceLoader resourceLoader;

    private static final int CHUNK_SIZE = 32;

    private Map<String, int[][]> backgroundChunks = new ConcurrentHashMap<>();
    private Map<String, int[][]> foregroundChunks = new ConcurrentHashMap<>();
    private Map<String, int[][]> animatedChunks = new ConcurrentHashMap<>();
//    private Map<String, List<ActorData>> actorChunks = new ConcurrentHashMap<>();

    public final List<ActorData> actorList = new ArrayList<>();

    public static final Map<Integer, Tile> tileList = new HashMap<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    public CompletableFuture<Void> init() {
        try {
            final var future = initWorld();
            future.thenRun(() -> {
                System.out.println("World initialized successfully");
                debugCheckMap();
            });
            return future;
        } catch (Exception e) {
            System.err.println("Failed to initialize world: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public WorldMap getWorldMap() {
        return new WorldMap(backgroundChunks, foregroundChunks, animatedChunks);
    }

    public CompletableFuture<Void> initWorld() {
        return CompletableFuture.runAsync(() -> {
            try {
                Resource resource = resourceLoader.getResource("classpath:static/tileMap/world.json");
                JsonNode json = objectMapper.readTree(resource.getInputStream());

                generateTiles(json).thenRun(() -> {
                    ParsedLayers parsedLayers = parseLayers(json);
                    this.backgroundChunks = parsedLayers.backgroundChunks;
                    this.foregroundChunks = parsedLayers.foregroundChunks;
                    this.animatedChunks = parsedLayers.animatedChunks;
//                    this.actorChunks = parsedLayers.actorChunks;
                });
            } catch (IOException e) {
                System.err.println("Error while parsing map: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private String getTilePosKey(int xPos, int yPos) {
        int col = xPos - (xPos % CHUNK_SIZE);
        int row = yPos - (yPos % CHUNK_SIZE);
        return Math.round(col) + "," + Math.round(row);
    }

//    public IndexingChunks getIndexingChunks(int x, int y) {
//        int[] viewportSize = calculateViewportSize(gameConfig.getDefaultTileScale());
//        int tilesX = viewportSize[0];
//        int tilesY = viewportSize[1];
//
//        int playerX = playerService.getPlayerX();
//        int playerY = playerService.getPlayerY();
//
//        int beforeY = playerY - tilesY * 2;
//        int afterY = playerY;
//        int beforeX = playerX - tilesX * 2;
//        int afterX = playerX;
//
//        WorldMap worldMap = getWorldMap();
//
//        Set<String> uniqueChunks = new HashSet<>();
//
//        for (int i = beforeY; i < afterY; i++) {
//            for (int j = beforeX; j < afterX; j++) {
//                String coords = getTilePosKey(j, i);
//                uniqueChunks.add(coords);
//            }
//        }
//
//        IndexingChunks indexingChunks = new IndexingChunks();
//
//        for (String key : uniqueChunks) {
//            ChunkCoords coords = keyToCoords(key);
//
//            if (worldMap.backgroundChunks.containsKey(key)) {
//                indexingChunks.backgroundChunks.add(new Chunk(coords.startX, coords.startY, worldMap.backgroundChunks.get(key)));
//            }
//            if (worldMap.foregroundChunks.containsKey(key)) {
//                indexingChunks.foregroundChunks.add(new Chunk(coords.startX, coords.startY, worldMap.foregroundChunks.get(key)));
//            }
//            if (worldMap.animatedChunks.containsKey(key)) {
//                indexingChunks.animatedChunks.add(new Chunk(coords.startX, coords.startY, worldMap.animatedChunks.get(key)));
//            }
//            if (worldMap.actorChunks.containsKey(key)) {
//                indexingChunks.actorEntities.addAll(worldMap.actorChunks.get(key));
//            }
//        }
//
//        return indexingChunks;
//    }

    private ChunkCoords keyToCoords(String key) {
        int commaIndex = key.indexOf(",");
        int startX = Integer.parseInt(key.substring(0, commaIndex));
        int startY = Integer.parseInt(key.substring(commaIndex + 1));
        return new ChunkCoords(startX, startY);
    }

    public Chunk getChunk(int posX, int posY, String layer) {
        String key = getTilePosKey(posX, posY);
        Map<String, int[][]> chunks;

        switch (layer) {
            case "background":
                chunks = getWorldMap().backgroundChunks;
                break;
            case "foreground":
                chunks = getWorldMap().foregroundChunks;
                break;
            case "animated":
                chunks = getWorldMap().animatedChunks;
                break;
            default:
                chunks = getWorldMap().backgroundChunks;
        }

        ChunkCoords coords = keyToCoords(key);
        return new Chunk(coords.startX, coords.startY, chunks.get(key));
    }

//    public Tile getTile(int posX, int posY, String layer) {
//        Chunk chunk = getChunk(posX, posY, layer);
//        int localY = Math.abs(posY % CHUNK_SIZE);
//        int localX = Math.abs(posX % CHUNK_SIZE);
//
//        // Check if chunk or position is valid
//        if (chunk.chunk == null || localY >= chunk.chunk.length || localX >= chunk.chunk[localY].length) {
//            return null; // Return null for invalid tile positions
//        }
//
//        int tileId = chunk.chunk[localY][localX];
//        return tileList.get(tileId);
//    }

    private int[][] convertToMatrix(ChunkData chunk) {
        int[][] matrix = new int[CHUNK_SIZE][CHUNK_SIZE];
        for (int y = 0; y < CHUNK_SIZE; y++) {
            System.arraycopy(chunk.data, y * CHUNK_SIZE, matrix[y], 0, CHUNK_SIZE);
        }
        return matrix;
    }

    private ParsedLayers parseLayers(JsonNode jsonData) {
        Map<String, int[][]> backgroundChunks = new ConcurrentHashMap<>();
        Map<String, int[][]> foregroundChunks = new ConcurrentHashMap<>();
        Map<String, int[][]> animatedChunks = new ConcurrentHashMap<>();
        Map<String, List<ActorData>> actorChunks = new ConcurrentHashMap<>();

        JsonNode layersNode = jsonData.get("layers");
        for (JsonNode layerNode : layersNode) {
            String layerName = layerNode.get("name").asText().toLowerCase();

            System.out.println("Processing layer: " + layerName);

            // Handle object layers (actors)
            if (layerNode.has("objects") && layerName.contains("actor")) {
                JsonNode objectsNode = layerNode.get("objects");
                for (JsonNode object : objectsNode) {
                    int x = object.get("x").asInt() / GameConfig.TILE_SIZE;
                    int y = object.get("y").asInt() / GameConfig.TILE_SIZE;
                    String name = object.has("name") ? object.get("name").asText() : "Unknown";

                    String key = getTilePosKey(x, y);

                    if (!actorChunks.containsKey(key)) {
                        actorChunks.put(key, new ArrayList<>());
                    }

                    ActorData actor = new ActorData(name, x, y);

                    // Process custom properties if they exist
                    if (object.has("properties")) {
                        for (JsonNode prop : object.get("properties")) {
                            String propName = prop.get("name").asText();
                            JsonNode valueNode = prop.get("value");
                            String propType = prop.has("type") ? prop.get("type").asText() : "string";

                            switch (propType) {
                                case "int":
                                case "float":
                                    actor.properties.put(propName, valueNode.asDouble());
                                    break;
                                case "bool":
                                    actor.properties.put(propName, valueNode.asBoolean());
                                    break;
                                default:
                                    actor.properties.put(propName, valueNode.asText());
                            }
                        }
                    }
                    this.actorList.add(actor);
                    actorChunks.get(key).add(actor);
                }
                continue; // Skip rest of processing for object layers
            }

            // Skip layers without chunks
            if (!layerNode.has("chunks") || layerNode.get("chunks").isEmpty()) {
                continue;
            }

            JsonNode chunksNode = layerNode.get("chunks");
            for (JsonNode chunkNode : chunksNode) {
                ChunkData chunk = new ChunkData();
                chunk.x = chunkNode.get("x").asInt();
                chunk.y = chunkNode.get("y").asInt();
                chunk.width = chunkNode.get("width").asInt();
                chunk.height = chunkNode.get("height").asInt();

                // Convert data array from JsonNode to int array
                JsonNode dataNode = chunkNode.get("data");
                chunk.data = new int[dataNode.size()];
                for (int i = 0; i < dataNode.size(); i++) {
                    chunk.data[i] = dataNode.get(i).asInt();
                }

                String key;
                if (chunk.width == CHUNK_SIZE && chunk.height == CHUNK_SIZE) {
                    key = chunk.x + "," + chunk.y;
                } else {
                    key = getTilePosKey(chunk.x, chunk.y);
                }

                int[][] matrix = convertToMatrix(chunk);

                if (layerName.contains("background")) {
                    backgroundChunks.put(key, matrix);
                } else if (layerName.contains("foreground")) {
                    foregroundChunks.put(key, matrix);
                } else if (layerName.contains("animated")) {
                    animatedChunks.put(key, matrix);
                }
            }
        }

        System.out.println("Loaded " + backgroundChunks.size() + " background chunks, " +
                foregroundChunks.size() + " foreground chunks, " +
                animatedChunks.size() + " animated chunks, " +
                actorChunks.size() + " actor chunks");

        return new ParsedLayers(backgroundChunks, foregroundChunks, animatedChunks);
    }

    public void debugCheckMap() {
        System.out.println("=== Map Debug Info ===");
        System.out.println("Total chunks - Background: " + backgroundChunks.size() +
                ", Foreground: " + foregroundChunks.size() +
                ", Animated: " + animatedChunks.size());
//                ", Actors: " + actorChunks.size());

        // Check several test points on the map
        int[][] testPoints = {
                {0, 0},
                {10, 10},
                {-10, -10},
                {100, 100},
                {-100, -100}
        };

        for (int[] point : testPoints) {
            System.out.println("\nDebug at point (" + point[0] + ", " + point[1] + "):");

            String key = getTilePosKey(point[0], point[1]);
            System.out.println("Chunk key: " + key);

            boolean hasBackground = backgroundChunks.containsKey(key);
            boolean hasForeground = foregroundChunks.containsKey(key);
            boolean hasAnimated = animatedChunks.containsKey(key);
//            boolean hasActors = actorChunks.containsKey(key);

            System.out.println("Has chunks - Background: " + hasBackground +
                    ", Foreground: " + hasForeground +
                    ", Animated: " + hasAnimated);
//                    ", Actors: " + hasActors);

            if (hasForeground) {
                try {
                    Chunk chunk = getChunk(point[0], point[1], "foreground");
                    int localX = Math.abs(point[0] % CHUNK_SIZE);
                    int localY = Math.abs(point[1] % CHUNK_SIZE);

                    if (chunk.chunk != null && chunk.chunk.length > localY &&
                            chunk.chunk[localY].length > localX) {
                        int tileId = chunk.chunk[localY][localX];
                        Tile tile = tileList.get(tileId);
                        if (tile != null) {
                            System.out.println("Tile at point: ID " + tileId +
                                    ", isWalkable: " + tile.props.isWalkable);
                        } else {
                            System.out.println("No tile found with ID: " + tileId);
                        }
                    } else {
                        System.out.println("No valid tile data at local position (" +
                                localX + ", " + localY + ") in chunk");
                    }
                } catch (Exception e) {
                    System.out.println("Error checking tile: " + e.getMessage());
                }
            }

            // Check for actors at this position
//            if (hasActors) {
//                List<ActorData> actors = actorChunks.get(key).stream()
//                        .filter(a -> a.x == point[0] && a.y == point[1])
//                        .collect(Collectors.toList());
//
//                if (!actors.isEmpty()) {
//                    System.out.println("Actors at this position: " + actors.size());
//                    for (ActorData actor : actors) {
//                        System.out.println("  - " + actor.name + " (properties: " + actor.properties + ")");
//                    }
//                } else {
//                    System.out.println("No actors at exact position");
//                }
//            }
        }

        System.out.println("=== End Map Debug Info ===");
    }

//    private int[] calculateViewportSize(double tileScale) {
//        int windowHeight = gameConfig.getWindowHeight();
//        int windowWidth = gameConfig.getWindowWidth();
//        double scaledTileSize = gameConfig.getTileSize() * tileScale;
//
//        int tilesY = (int) Math.round(windowHeight / (scaledTileSize * gameConfig.getTileSize()) / 2);
//        int tilesX = (int) Math.round(windowWidth / (scaledTileSize * gameConfig.getTileSize()) / 2);
//
//        return new int[] {tilesX, tilesY};
//    }

    public CompletableFuture<Void> generateTiles(JsonNode mapData) {
        return CompletableFuture.runAsync(() -> {
            try {
                JsonNode tilesetsNode = mapData.get("tilesets");
                for (JsonNode tilesetNode : tilesetsNode) {
                    String tilesetName = tilesetNode.get("name").asText();
                    int firstGid = tilesetNode.get("firstgid").asInt();
                    int tileCount = tilesetNode.get("tilecount").asInt();
                    int tileWidth = tilesetNode.get("tilewidth").asInt();
                    int tileHeight = tilesetNode.get("tileheight").asInt();
                    String imagePath = tilesetNode.get("image").asText();
                    int imageWidth = tilesetNode.get("imagewidth").asInt();
                    int imageHeight = tilesetNode.get("imageheight").asInt();

                    // Extract filename from path
                    String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
                    Resource imageResource = resourceLoader.getResource("classpath:static/tileMap/forGeneration/" + imageName);
                    BufferedImage tilesetImage = ImageIO.read(imageResource.getInputStream());

                    int columns = imageWidth / tileWidth;

                    for (int localId = 0; localId < tileCount; localId++) {
                        int globalId = localId + firstGid;
                        String name = "Tile" + globalId;

                        // Check for tile-specific data
                        JsonNode tilesNode = tilesetNode.get("tiles");
                        JsonNode tileData = null;

                        if (tilesNode != null) {
                            for (JsonNode tile : tilesNode) {
                                if (tile.get("id").asInt() == localId) {
                                    tileData = tile;
                                    break;
                                }
                            }
                        }

                        // Calculate tile position in the tileset image
                        int col = localId % columns;
                        int row = localId / columns;

                        // Extract the tile image from the tileset
                        BufferedImage tileImage = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
                        tileImage.getGraphics().drawImage(
                                tilesetImage,
                                0, 0, tileWidth, tileHeight,
                                col * tileWidth, row * tileHeight, (col + 1) * tileWidth, (row + 1) * tileHeight,
                                null
                        );

                        // Create tile properties
                        TileProps props = new TileProps(true, false, 0, false);

                        // Process custom name and properties if available
                        if (tileData != null) {
                            JsonNode propertiesNode = tileData.get("properties");
                            if (propertiesNode != null) {
                                for (JsonNode property : propertiesNode) {
                                    String propName = property.get("name").asText();
                                    JsonNode valueNode = property.get("value");

                                    if ("name".equals(propName)) {
                                        name = valueNode.asText();
                                    } else if ("isWalkable".equals(propName)) {
                                        props.isWalkable = valueNode.asBoolean();
                                    } else if ("renderAfter".equals(propName)) {
                                        props.renderAfter = valueNode.asBoolean();
                                    } else if ("damage".equals(propName)) {
                                        props.damage = valueNode.asInt();
                                    } else if ("animated".equals(propName)) {
                                        props.animated = valueNode.asBoolean();
                                    }
                                }
                            }

                            // Process animation frames if available
                            JsonNode animationNode = tileData.get("animation");
                            if (animationNode != null) {
                                props.animated = true;
                                List<AnimationFrame> frames = new ArrayList<>();

                                for (JsonNode frame : animationNode) {
                                    int tileId = frame.get("tileid").asInt();
                                    int duration = frame.get("duration").asInt();
                                    frames.add(new AnimationFrame(tileId, duration));
                                }

                                // Store animation frames (implementation depends on your game engine)
                            }
                        }

                        // Create and store the tile
                        Tile tile = new Tile(name, props, tileImage);
                        tileList.put(globalId, tile);
                    }
                }

                System.out.println("Generated " + tileList.size() + " tiles");

            } catch (IOException e) {
                System.out.println("Error generating tiles: " + e.getMessage());
//                e.printStackTrace();
            }
        });
    }

    // Supporting classes

    public static class WorldMap {
        public final Map<String, int[][]> backgroundChunks;
        public final Map<String, int[][]> foregroundChunks;
        public final Map<String, int[][]> animatedChunks;
//        public final Map<String, List<ActorData>> actorChunks;

        public WorldMap(Map<String, int[][]> backgroundChunks,
                        Map<String, int[][]> foregroundChunks,
                        Map<String, int[][]> animatedChunks) {
            this.backgroundChunks = backgroundChunks;
            this.foregroundChunks = foregroundChunks;
            this.animatedChunks = animatedChunks;
//            this.actorChunks = actorChunks;
        }
    }

    public static class ChunkData {
        public int[] data;
        public int width;
        public int height;
        public int x;
        public int y;
    }

    public static class ChunkCoords {
        public final int startX;
        public final int startY;

        public ChunkCoords(int startX, int startY) {
            this.startX = startX;
            this.startY = startY;
        }
    }

    public static class Chunk {
        public final int startX;
        public final int startY;
        public final int[][] chunk;

        public Chunk(int startX, int startY, int[][] chunk) {
            this.startX = startX;
            this.startY = startY;
            this.chunk = chunk;
        }
    }

    public static class IndexingChunks {
        public List<Chunk> backgroundChunks = new ArrayList<>();
        public List<Chunk> foregroundChunks = new ArrayList<>();
        public List<Chunk> animatedChunks = new ArrayList<>();
        public List<ActorData> actorEntities = new ArrayList<>();
    }

    public static class ParsedLayers {
        public final Map<String, int[][]> backgroundChunks;
        public final Map<String, int[][]> foregroundChunks;
        public final Map<String, int[][]> animatedChunks;
//        public final Map<String, List<ActorData>> actorChunks;

        public ParsedLayers(Map<String, int[][]> backgroundChunks,
                            Map<String, int[][]> foregroundChunks,
                            Map<String, int[][]> animatedChunks) {
            this.backgroundChunks = backgroundChunks;
            this.foregroundChunks = foregroundChunks;
            this.animatedChunks = animatedChunks;
//            this.actorChunks = actorChunks;
        }
    }

    public static class ActorData {
        public final String name;
        public final int x;
        public final int y;
        public final Map<String, Object> properties = new HashMap<>();

        public ActorData(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "ActorData{name='" + name + "', x=" + x + ", y=" + y + "}";
        }
    }

    public static class AnimationFrame {
        public final int tileId;
        public final int duration;

        public AnimationFrame(int tileId, int duration) {
            this.tileId = tileId;
            this.duration = duration;
        }
    }
}