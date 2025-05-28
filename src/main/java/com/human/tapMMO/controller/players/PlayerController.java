package com.human.tapMMO.controller.players;

import com.human.tapMMO.model.connection.InitCharacterConnection;
import com.human.tapMMO.model.tables.*;
import com.human.tapMMO.model.tables.Character;
import com.human.tapMMO.service.auth.CustomUserDetailsService;
import com.human.tapMMO.service.game.GameLoopService;
import com.human.tapMMO.service.game.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/player")
public class PlayerController {
    private final PlayerService playerService;
    private final GameLoopService gameLoopService;

    @PostMapping("/createChar")
    public ResponseEntity<InitCharacterConnection> createNewCharacter(@RequestBody InitCharacterConnection init, Authentication authentication) throws Exception {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetailsService.CustomUserDetails userDetails) {
            return ResponseEntity.ok(playerService.initNewCharacter(init, userDetails.getId()));
        }
        System.out.println("unauthorized "+init.getName());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/getCharList")
    public ResponseEntity<?> getCharacters(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetailsService.CustomUserDetails userDetails) {
            var chars = playerService.getCharactersByAccountId(userDetails.getId());
            System.out.println("response: " + chars.size());
            if (!chars.isEmpty()) {
                return ResponseEntity.ok(chars);
            }
        }
        System.out.println("unauthorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/getCharData")
    public ResponseEntity<?> getCharacterData(@RequestParam long characterId, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetailsService.CustomUserDetails userDetails) {
            final var characterData = playerService.getAllCharacterData(characterId);
            gameLoopService.addNewPlayer(characterData);
            System.out.println("sent char data to " + characterData.getName());
            return ResponseEntity.ok(characterData);
        }
        System.out.println("unauthorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/updateStats")
    public ResponseEntity updateCharacterStats(@RequestBody CharacterStats characterStats) {
        playerService.updateCharacterStats(characterStats);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/updateChar")
    public ResponseEntity updateCharacter(@RequestBody Character character) {
        playerService.updateCharacter(character);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteChar")
    public ResponseEntity deleteCharacter(@RequestBody long characterId) {
        playerService.deleteCharacter(characterId);
        return ResponseEntity.ok().build();
    }

    // Дополнительные эндпоинты для проверки требований
    @GetMapping("/{characterId}/requirements/level")
    public ResponseEntity<Boolean> checkLevelRequirement(@PathVariable Long characterId, @RequestParam int requiredLevel) {
        boolean hasLevel = playerService.hasRequiredLevel(characterId, requiredLevel);
        return ResponseEntity.ok(hasLevel);
    }

    @GetMapping("/{characterId}/requirements/attribute")
    public ResponseEntity<Boolean> checkAttributeRequirement(@PathVariable Long characterId,
                                                             @RequestParam String attributeName,
                                                             @RequestParam int requiredValue) {
        boolean hasAttribute = playerService.hasRequiredAttributeLevel(characterId, attributeName, requiredValue);
        return ResponseEntity.ok(hasAttribute);
    }

    @GetMapping("/{characterId}/requirements/skill")
    public ResponseEntity<Boolean> checkSkillRequirement(@PathVariable Long characterId,
                                                         @RequestParam String skillName,
                                                         @RequestParam int requiredValue) {
        boolean hasSkill = playerService.hasRequiredSkillExperience(characterId, skillName, requiredValue);
        return ResponseEntity.ok(hasSkill);
    }

    @GetMapping("/{characterId}/requirements/quest")
    public ResponseEntity<Boolean> checkQuestRequirement(@PathVariable Long characterId, @RequestParam String questName) {
        boolean hasQuest = playerService.hasCompletedQuest(characterId, questName);
        return ResponseEntity.ok(hasQuest);
    }

    @GetMapping("/{characterId}/requirements/item")
    public ResponseEntity<Boolean> checkItemRequirement(@PathVariable Long characterId,
                                                        @RequestParam String itemId,
                                                        @RequestParam int quantity) {
        boolean hasItem = playerService.hasItem(characterId, itemId, quantity);
        return ResponseEntity.ok(hasItem);
    }

    @PostMapping("/{characterId}/attributes/modify")
    public ResponseEntity<?> modifyAttribute(@PathVariable Long characterId,
                                             @RequestParam String attributeName,
                                             @RequestParam double value) {
        playerService.applyAttributeModifier(characterId, attributeName, value);
        return ResponseEntity.ok().build();
    }
}