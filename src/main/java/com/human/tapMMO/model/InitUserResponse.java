package com.human.tapMMO.model;

import lombok.Data;

@Data
public class InitUserResponse {
    private String requestId;
    private InitCharacterConnection characterData;
}
