package com.human.tapMMO.controller.players;

import com.human.tapMMO.model.tables.Talent;
import com.human.tapMMO.service.game.player.TalentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/talents")
@RequiredArgsConstructor
public class TalentController {

    private final TalentService talentService;

    @GetMapping
    public ResponseEntity<List<Talent>> getAllTalents() {
        return ResponseEntity.ok(talentService.getAllTalents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Talent> getTalentById(@PathVariable Long id) {
        Optional<Talent> talent = talentService.getTalentById(id);
        return talent.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Talent> getTalentByName(@PathVariable String name) {
        Optional<Talent> talent = talentService.getTalentByName(name);
        return talent.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Talent> createTalent(@RequestParam String name) {
        Talent talent = talentService.createTalent(name);
        return ResponseEntity.ok(talent);
    }

    @DeleteMapping("/{talentId}")
    public ResponseEntity<?> deleteTalent(@PathVariable Long talentId) {
        talentService.deleteTalent(talentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/character/{characterId}/can-learn/{talentId}")
    public ResponseEntity<Boolean> canLearnTalent(@PathVariable Long characterId, @PathVariable Long talentId) {
        boolean canLearn = talentService.canLearnTalent(characterId, talentId);
        return ResponseEntity.ok(canLearn);
    }
}