package com.human.tapMMO.mapper;

import com.human.tapMMO.dto.rest.ItemDTO;
import com.human.tapMMO.model.tables.EquippedItem;
import com.human.tapMMO.model.tables.InventoryItem;
import com.human.tapMMO.model.tables.Item;
import com.human.tapMMO.model.tables.ItemPosition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {
    Item toEntity(ItemDTO itemDTO); //toItem

    @Mapping(target = "itemId", source = "itemDTO.id")
    ItemPosition toPosition(ItemDTO itemDTO);

    @Mapping(target = "itemId", source = "itemDTO.id")
    InventoryItem toInventory(ItemDTO itemDTO);

    @Mapping(target = "itemId", source = "itemDTO.id")
    EquippedItem toEquipped(ItemDTO itemDTO);

    @Mapping(target = "id", source = "item.id")
    ItemDTO toItemDTO(Item item, ItemPosition itemPosition, InventoryItem inventoryItem);
}