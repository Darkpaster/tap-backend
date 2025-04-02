package com.human.tapMMO.dto;

import lombok.Data;

@Data
public class InitUserResponseDTO {
    private String requestId;
    private CharacterDTO characterData;
}
