package com.human.tapMMO.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class JWTResponse implements Serializable {
    private String token;

    public JWTResponse(String token) {
        this.token = token;
    }
}
