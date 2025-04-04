package com.human.tapMMO.controller;

import com.human.tapMMO.model.InitCharacterConnection;
import com.human.tapMMO.model.tables.Character;
import com.human.tapMMO.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @PostMapping("/create")
    public ResponseEntity<InitCharacterConnection> createNewCharacter(@RequestBody InitCharacterConnection init) {
        return ResponseEntity.ok(playerService.initNewCharacter(init));
    }

    @GetMapping("/get")
    public ResponseEntity<Character> getCharacter(@RequestBody long charId) {
        return ResponseEntity.ok(playerService.getCharacterByID(charId).orElseThrow());
    }

//    @GetMapping
//    public ResponseEntity<List<Character>> getCharacterList(@PathVariable long userID) {
//        return ResponseEntity.ok(null);
//    }
}
