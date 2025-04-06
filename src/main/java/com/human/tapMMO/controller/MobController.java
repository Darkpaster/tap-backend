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

    private final List<DeferredResult<String>> clients = new CopyOnWriteArrayList<>();

    @PostMapping("/kill")
    public DeferredResult<String> kill(@RequestBody long mobId) {
        final var deadMob = mobService.die(mobId);
        DeferredResult<String> output = new DeferredResult<>(30_000L, "timeout"); // 10 секунд таймаут
        clients.add(output); //надо чтобы ответ получали все игроки, а не только убивший моба

        output.onCompletion(() -> clients.remove(output));
        mobService.spawn(deadMob);
        return output;
    }

    @GetMapping("/init")
    public ResponseEntity<List<Mob>> initAllMobs() {
        return ResponseEntity.ok(mobService.initAllMobs());
    }
}
