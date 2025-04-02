package com.human.tapMMO.dto;

import com.human.tapMMO.model.entities.Character;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link Character}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDTO {
    long id;
    String name;
    int x;
    int y;
    String renderState;
    String bubble;
    String roomID;
}