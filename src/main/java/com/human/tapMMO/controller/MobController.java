package com.human.tapMMO.controller;

import com.human.tapMMO.model.tables.Mob;
import com.human.tapMMO.service.MobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mob")
public class MobController {
    private final MobService mobService;

//    private final List<DeferredResult<String>> clients = new CopyOnWriteArrayList<>();
    @GetMapping("/init")
    public ResponseEntity<List<Mob>> initAllMobs() {
        return ResponseEntity.ok(mobService.initAllMobs());
    }


}
