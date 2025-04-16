package com.human.tapMMO.controller;

import com.human.tapMMO.model.tables.Talent;
import com.human.tapMMO.service.game.TalentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/talent")
public class TalentController {

    private final TalentService talentService;

    @Autowired
    public TalentController(TalentService talentService) {
        this.talentService = talentService;
    }

    @GetMapping
    public List<Talent> getAllTalents() {
        return talentService.getAllTalents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Talent> getTalentById(@PathVariable Long id) {
        Optional<Talent> talent = talentService.getTalentById(id);
        return talent.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public List<Talent> getTalentsByCategory(@PathVariable TalentCategory category) {
        return talentService.getTalentsByCategory(category);
    }

    @GetMapping("/subcategory/{subCategory}")
    public List<Talent> getTalentsBySubCategory(@PathVariable TalentSubCategory subCategory) {
        return talentService.getTalentsBySubCategory(subCategory);
    }

    @GetMapping("/specialization/{specialization}")
    public List<Talent> getTalentsBySpecialization(@PathVariable TalentSpecialization specialization) {
        return talentService.getTalentsBySpecialization(specialization);
    }

    @GetMapping("/character/{characterId}/available")
    public List<Talent> getAvailableTalents(@PathVariable Long characterId) {
        return talentService.getAvailableTalents(characterId);
    }

    @PostMapping("/character/{characterId}/learn/{talentId}")
    public ResponseEntity<?> learnTalent(@PathVariable Long characterId, @PathVariable Long talentId) {
        boolean success = talentService.learnTalent(characterId, talentId);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Cannot learn talent: requirements not met");
        }
    }
}
