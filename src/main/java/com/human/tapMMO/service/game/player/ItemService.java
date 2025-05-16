package com.human.tapMMO.service.game.player;

import com.human.tapMMO.dto.rest.ItemDTO;
import com.human.tapMMO.mapper.ItemMapper;
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
    private final ItemMapper itemMapper;


    public void equipItem(ItemDTO itemDTO) {
        inventoryItemRepository.findById(itemDTO.getId()).orElseThrow(() -> new NoSuchElementException("equip item"));
        inventoryItemRepository.deleteByItemId(itemDTO.getId());
//        final var newEquippedItem = new EquippedItem();
        final var item = itemRepository.findById(itemDTO.getId()).orElseThrow(() -> new NoSuchElementException("equip item primary"));;
//        newEquippedItem.setEquippedSlot(item.getEquipSlot());
//        newEquippedItem.setItemId(inventoryItem.getItemId());
        equippedItemRepository.save(itemMapper.toEquipped(itemDTO));
    }

    public void unequipItem(EquippedItem equippedItem, short inventorySlot) {
        equippedItemRepository.findById(equippedItem.getId()).orElseThrow(() -> new NoSuchElementException("unequip item"));
        equippedItemRepository.deleteById(equippedItem.getId());
        final var newInventoryItem = new InventoryItem();
        newInventoryItem.setItemId(equippedItem.getItemId());
        newInventoryItem.setInventorySlot(inventorySlot);
        inventoryItemRepository.save(newInventoryItem);
    }

    public long dropItem(ItemDTO itemDTO) {
        inventoryItemRepository.deleteById(inventoryItemRepository.findByItemId(itemDTO.getId()).orElseThrow(() -> new NoSuchElementException("on drop item")).getId());
        final var posItem = itemMapper.toPosition(itemDTO);
        posItem.setItemId(itemDTO.getId());
        itemPositionRepository.saveAndFlush(posItem);
        return posItem.getId();
    }

    public void pickUpItem(InventoryItem inventoryItem) {
        final var posId = itemPositionRepository.getByItemId(inventoryItem.getItemId()).orElseThrow(() -> new NoSuchElementException("pick up item"));
        itemPositionRepository.deleteById(posId.getId());
        inventoryItemRepository.save(inventoryItem);
    }

    public boolean isItemExist(long itemId) {
        return itemRepository.existsById(itemId);
    }

    public long lootItem(ItemDTO itemDTO) { //при убийстве моба
//        newItem.setItemType(itemPosition.getItemType());
//        newItem.setEquipSlot(itemPosition.getEquipSlot());
        var newItem = itemRepository.saveAndFlush(itemMapper.toEntity(itemDTO));
        var posItem = itemMapper.toPosition(itemDTO);
        posItem.setItemId(newItem.getId());
        itemPositionRepository.saveAndFlush(posItem);
        return posItem.getId();
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
