package com.human.tapMMO.controller.players;

import com.human.tapMMO.model.tables.Profession;
import com.human.tapMMO.service.game.player.ProfessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/professions")
@RequiredArgsConstructor
public class ProfessionController {

    private final ProfessionService professionService;

    @GetMapping("/character/{characterId}")
    public ResponseEntity<List<Profession>> getCharacterProfessions(@PathVariable Long characterId) {
        List<Profession> professions = professionService.getCharacterProfessions(characterId);
        return ResponseEntity.ok(professions);
    }

    @PostMapping("/character/{characterId}/learn")
    public ResponseEntity<Profession> learnProfession(@PathVariable Long characterId, @RequestParam String professionName) {
        try {
            Profession profession = professionService.learnProfession(characterId, professionName);
            return ResponseEntity.ok(profession);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/profession/{professionId}/experience")
    public ResponseEntity<?> addExperience(@PathVariable Long professionId, @RequestParam int experience) {
        professionService.addExperience(professionId, experience);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profession/{professionId}")
    public ResponseEntity<Profession> getProfessionById(@PathVariable Long professionId) {
        Optional<Profession> profession = professionService.getProfessionById(professionId);
        return profession.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/profession/{professionId}")
    public ResponseEntity<?> deleteProfession(@PathVariable Long professionId) {
        professionService.deleteProfession(professionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/character/{characterId}/profession/{professionName}/level")
    public ResponseEntity<Integer> getProfessionLevel(@PathVariable Long characterId, @PathVariable String professionName) {
        int level = professionService.getProfessionLevel(characterId, professionName);
        return ResponseEntity.ok(level);
    }
}