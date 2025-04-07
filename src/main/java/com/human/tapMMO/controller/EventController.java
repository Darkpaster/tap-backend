package com.human.tapMMO.controller;

import com.human.tapMMO.model.tables.Mob;
import com.human.tapMMO.service.MobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/event")
public class EventController { // для лонг пулинга, но пока не нужен
//    private final MobService mobService;
//
//    private final List<DeferredResult<String>> clients = new CopyOnWriteArrayList<>();
//
//    @PostMapping("/mob/kill")
//    public DeferredResult<String> kill(@RequestBody long mobId) {
//        final var deadMob = mobService.die(mobId);
//        DeferredResult<String> output = new DeferredResult<>(30_000L, "timeout"); // 10 секунд таймаут
//        clients.add(output); //надо чтобы ответ получали все игроки, а не только убивший моба
//
////        output.onCompletion(() -> clients.remove(output));
////        mobService.spawn(deadMob);
//    }
//
//    @PostMapping("/mob")
//    private String mobEvent(@RequestParam String message) {
//        for (DeferredResult<String> client : clients) {
//            client.setResult(message); // spawn|die
//        }
//        clients.clear();
//        return "Sent to all";
//    }

}
