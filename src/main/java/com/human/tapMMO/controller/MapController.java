package com.human.tapMMO.controller;

import com.human.tapMMO.dto.ApiResponse;
import com.human.tapMMO.runtime.game.world.MapManager;
import com.human.tapMMO.runtime.game.world.Tile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapController {
    @Autowired
    private final MapManager mapManager;

    @GetMapping
    public ResponseEntity<MapManager.WorldMap> getWorld() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(mapManager.getWorldMap());
    }

    @GetMapping("/tiles")
    public ResponseEntity<Map<Integer, Tile>> getTiles() {
        System.out.println("tiles: "+MapManager.tileList.size());
        return ResponseEntity.status(HttpStatus.OK)
                .body(MapManager.tileList);
    }
}
