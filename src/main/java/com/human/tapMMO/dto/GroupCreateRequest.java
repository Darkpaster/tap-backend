package com.human.tapMMO.dto;

import lombok.Data;

@Data
public class GroupCreateRequest {
    private String name;
    private GroupType groupType;
}
