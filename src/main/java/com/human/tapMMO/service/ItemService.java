package com.human.tapMMO.service;

import com.human.tapMMO.model.tables.EquippedItem;
import com.human.tapMMO.model.tables.InventoryItem;
import com.human.tapMMO.model.tables.Item;
import com.human.tapMMO.model.tables.ItemPosition;
import com.human.tapMMO.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemPositionRepository itemPositionRepository;
    private final ItemStatsRepository itemStatsRepository;
    private final EquippedItemRepository equippedItemRepository;
    private final InventoryItemRepository inventoryItemRepository;


    public void equipItem(InventoryItem inventoryItem) {
        inventoryItemRepository.findById(inventoryItem.getId()).orElseThrow(() -> new NoSuchElementException("equip item"));
        inventoryItemRepository.deleteById(inventoryItem.getId());
        final var newEquippedItem = new EquippedItem();
        final var item = itemRepository.findById(inventoryItem.getItemId()).orElseThrow(() -> new NoSuchElementException("equip item primary"));;
        newEquippedItem.setEquippedSlot(item.getEquipSlot());
        newEquippedItem.setItemId(inventoryItem.getItemId());
        equippedItemRepository.save(newEquippedItem);
    }

    public void unequipItem(EquippedItem equippedItem, short inventorySlot) {
        equippedItemRepository.findById(equippedItem.getId()).orElseThrow(() -> new NoSuchElementException("unequip item"));
        equippedItemRepository.deleteById(equippedItem.getId());
        final var newInventoryItem = new InventoryItem();
        newInventoryItem.setItemId(equippedItem.getItemId());
        newInventoryItem.setInventorySlot(inventorySlot);
        inventoryItemRepository.save(newInventoryItem);
    }

    public void dropItem(InventoryItem inventoryItem, ItemPosition itemPosition) {
        inventoryItemRepository.findByItemId(inventoryItem.getItemId()).orElseThrow(() -> new NoSuchElementException("drop item"));
        inventoryItemRepository.deleteById(inventoryItem.getId());
        itemPositionRepository.save(itemPosition);
    }

    public void pickUpItem(InventoryItem inventoryItem) {
        final var posId = itemPositionRepository.getByItemId(inventoryItem.getItemId()).orElseThrow(() -> new NoSuchElementException("pick up item"));
        itemPositionRepository.deleteById(posId.getId());
        inventoryItemRepository.save(inventoryItem);
    }

    public void lootItem(ItemPosition itemPosition, Item item) { //при убийстве моба
        final var savedItem = itemRepository.saveAndFlush(item);
        itemPosition.setItemId(savedItem.getId());
        itemPositionRepository.save(itemPosition);
    }

    public void deleteItem(long itemId) {
        itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException("remove item"));
        itemRepository.deleteById(itemId);
        final var inventoryItem = inventoryItemRepository.findByItemId(itemId);
        inventoryItem.ifPresent(inventoryItemRepository::delete);
    }

    public List<ItemPosition> initAllItems() {
        return itemPositionRepository.findAll();
    }
}
