package com.human.tapMMO.controller;

import com.human.tapMMO.model.tables.Profession;
import com.human.tapMMO.service.game.ProfessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professions")
@RequiredArgsConstructor
public class ProfessionController {

    private final ProfessionService professionService;

    @GetMapping
    public ResponseEntity<List<Profession>> getAllProfessions() {
        return ResponseEntity.ok(professionService.getAllProfessions());
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<PlayerProfession>> getPlayerProfessions(@PathVariable Long playerId) {
        return ResponseEntity.ok(professionService.getPlayerProfessions(playerId));
    }

    @PostMapping("/learn")
    public ResponseEntity<PlayerProfession> learnProfession(@RequestBody LearnProfessionRequest request) {
        return ResponseEntity.ok(professionService.learnProfession(
                request.getPlayerId(),
                request.getProfessionId()
        ));
    }

    @PostMapping("/recipe/learn")
    public ResponseEntity<?> learnRecipe(@RequestBody LearnRecipeRequest request) {
        professionService.learnRecipe(
                request.getPlayerProfessionId(),
                request.getRecipeId()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{playerProfessionId}/recipes/available")
    public ResponseEntity<List<ProfessionRecipe>> getAvailableRecipes(@PathVariable Long playerProfessionId) {
        return ResponseEntity.ok(professionService.getAvailableRecipes(playerProfessionId));
    }

    @GetMapping("/{playerProfessionId}/recipes/learnable")
    public ResponseEntity<List<ProfessionRecipe>> getLearnableRecipes(@PathVariable Long playerProfessionId) {
        return ResponseEntity.ok(professionService.getLearnableRecipes(playerProfessionId));
    }

    @PostMapping("/craft/start")
    public ResponseEntity<Crafting> startCrafting(@RequestBody StartCraftingRequest request) {
        return ResponseEntity.ok(professionService.startCrafting(
                request.getPlayerProfessionId(),
                request.getRecipeId(),
                request.getQuantity()
        ));
    }

    @PostMapping("/craft/{craftingId}/complete")
    public ResponseEntity<?> completeCrafting(@PathVariable Long craftingId) {
        professionService.completeCrafting(craftingId);
        return ResponseEntity.ok().build();
    }

    // Admin endpoints for managing professions and recipes would go here
}

