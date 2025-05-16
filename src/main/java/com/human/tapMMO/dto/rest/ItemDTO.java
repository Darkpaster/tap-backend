package com.human.tapMMO.dto.rest;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.mapstruct.Mapper;

@Data
public class ItemDTO {
    private Long id;
    private String name;
    private String equipmentSlot;
    private short inventorySlot;
    private float x;
    private float y;
}
