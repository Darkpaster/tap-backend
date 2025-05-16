package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Getter
@Setter
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;
    @Column(name = "name", nullable = false, updatable = false)
    private String name;
    @Column(name = "equipment_slot", updatable = false)
    @Pattern(regexp = "head|shoulders|body|cape|braces|weapon1|weapon2|weapon1&2|gloves|belt|legs|boots|ring|accessory|none")
    private String equipmentSlot;
}
