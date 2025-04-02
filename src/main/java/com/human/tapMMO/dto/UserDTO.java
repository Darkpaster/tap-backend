package com.human.tapMMO.dto;

import lombok.Data;
import lombok.Value;


@Value
public class UserDTO {
    String email;
    String username;
    String password;
}
