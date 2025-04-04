package com.human.tapMMO.model;

import com.human.tapMMO.model.tables.Character;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link Character}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitCharacterConnection {
    long id;
    String name;
    String roomId;
}