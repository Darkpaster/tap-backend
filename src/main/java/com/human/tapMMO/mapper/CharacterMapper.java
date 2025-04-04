package com.human.tapMMO.mapper;

import com.human.tapMMO.model.tables.Character;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CharacterMapper {
    Character toEntity(CharacterDTO characterDTO);

    CharacterDTO toCharacterDTO(Character character);
}