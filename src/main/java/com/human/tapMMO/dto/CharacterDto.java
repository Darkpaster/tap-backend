package com.human.tapMMO.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.human.tapMMO.model.tables.Character}
 */
@Value
public class CharacterDto {
    int x;
    int y;
    String nickname;
    int level;
    @Pattern(regexp = "wanderer|samurai|knight|werewolf|mage")
    String characterType;
    LocalDateTime creationDate;
    LocalDateTime lastLogin;
}