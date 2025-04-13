package com.human.tapMMO.controller;

import com.human.tapMMO.model.tables.ItemPosition;
import com.human.tapMMO.service.game.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
