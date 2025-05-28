package com.human.tapMMO.controller.world;

import com.human.tapMMO.dto.websocket.ActorDTO;
import com.human.tapMMO.mapper.ActorMapper;
import com.human.tapMMO.service.game.GameLoopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mob")
public class MobController {
    private final GameLoopService gameLoopService;
    private final ActorMapper actorMapper;

//    private final List<DeferredResult<String>> clients = new CopyOnWriteArrayList<>();
    @GetMapping("/init")
    public ResponseEntity<List<ActorDTO>> initAllMobs() {
        System.out.println("mobs init: "+gameLoopService.getMobList().size());
        return ResponseEntity.ok((actorMapper.toActorDTOFromMob(new ArrayList<>(gameLoopService.getMobList().values()))));
    }


}
