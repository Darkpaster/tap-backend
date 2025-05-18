package com.human.tapMMO.controller;

import com.human.tapMMO.dto.websocket.ActorDTO;
import com.human.tapMMO.model.tables.MobModel;
import com.human.tapMMO.service.game.world.MobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mob")
public class MobController {
    private final MobService mobService;

//    private final List<DeferredResult<String>> clients = new CopyOnWriteArrayList<>();
    @GetMapping("/init")
    public ResponseEntity<List<ActorDTO>> initAllMobs() {
        var mobs = mobService.initAllMobs();
        System.out.println("mobs init: "+mobs.size());
        return ResponseEntity.ok(mobs);
    }


}
