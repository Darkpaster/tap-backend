package com.human.tapMMO.mapper;

import com.human.tapMMO.dto.UserDTO;
import com.human.tapMMO.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toEntity(UserDTO clientDTO);

    UserDTO toDTO(User clientModel);
}
