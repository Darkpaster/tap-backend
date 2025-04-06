package com.human.tapMMO.controller;

import com.human.tapMMO.model.InitCharacterConnection;
import com.human.tapMMO.model.tables.*;
import com.human.tapMMO.model.tables.Character;
import com.human.tapMMO.service.CustomUserDetailsService;
import com.human.tapMMO.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/player")
public class PlayerController {
    private final PlayerService playerService;

    @PostMapping("/create")
    public ResponseEntity<InitCharacterConnection> createNewCharacter(@RequestBody InitCharacterConnection init, Authentication authentication) throws Exception {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetailsService.CustomUserDetails userDetails) {
            return ResponseEntity.ok(playerService.initNewCharacter(init, userDetails.getId()));
        }
        System.out.println("unauthorized");
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
    public ResponseEntity updateCharacter(@RequestBody long characterId) {
        playerService.deleteCharacter(characterId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/equipItem")
    public ResponseEntity equipItem(@RequestBody InventoryItem inventoryItem) {
        playerService.equipItem(inventoryItem);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unequipItem")
    public ResponseEntity unequipItem(@RequestBody EquippedItem equippedItem, short inventorySlot) {
        playerService.unequipItem(equippedItem, inventorySlot);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dropItem")
    public ResponseEntity dropItem(@RequestBody InventoryItem inventoryItem, ItemPosition itemPosition) {
        playerService.dropItem(inventoryItem, itemPosition);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pickUpItem")
    public ResponseEntity pickUpItem(@RequestBody InventoryItem inventoryItem) {
        playerService.pickUpItem(inventoryItem);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lootItem")
    public ResponseEntity lootItem(@RequestBody ItemPosition itemPosition) {
        playerService.lootItem(itemPosition);
        return ResponseEntity.ok().build();
    }
}
