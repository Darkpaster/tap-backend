package com.human.tapMMO.controller;

import com.human.tapMMO.dto.rest.ItemDTO;
import com.human.tapMMO.model.tables.EquippedItem;
import com.human.tapMMO.model.tables.InventoryItem;
import com.human.tapMMO.model.tables.ItemPosition;
import com.human.tapMMO.service.game.player.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/init")
    public ResponseEntity<List<ItemPosition>> initAllMobs() {
        System.out.println("items init");
        return ResponseEntity.ok(itemService.initAllItems());
    }


    @PostMapping("/equipItem")
    public ResponseEntity equipItem(@RequestBody ItemDTO itemDTO) {
        itemService.equipItem(itemDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unequipItem")
    public ResponseEntity unequipItem(@RequestBody EquippedItem equippedItem, short inventorySlot) {
        itemService.unequipItem(equippedItem, inventorySlot);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dropItem")
    public ResponseEntity dropItem(@RequestBody ItemDTO itemDTO) {
        itemService.dropItem(itemDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pickUpItem")
    public ResponseEntity pickUpItem(@RequestBody InventoryItem inventoryItem) {
        itemService.pickUpItem(inventoryItem);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lootItem")
    public ResponseEntity lootItem(@RequestBody ItemDTO itemDTO) {
        itemService.lootItem(itemDTO);
        return ResponseEntity.ok().build();
    }
}
