package com.human.tapMMO.model.connection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitCharacterConnection {
    long characterId;
    private String nickname;
    String roomId;
    String characterType;
}