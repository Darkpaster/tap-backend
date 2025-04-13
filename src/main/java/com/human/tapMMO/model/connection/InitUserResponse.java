package com.human.tapMMO.model.connection;

import lombok.Data;

@Data
public class InitUserResponse {
    private String requestId;
    private InitCharacterConnection characterData;
}
