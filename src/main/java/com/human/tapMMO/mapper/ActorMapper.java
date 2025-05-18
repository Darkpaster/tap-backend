package com.human.tapMMO.mapper;

import com.human.tapMMO.dto.websocket.ActorDTO;
import com.human.tapMMO.model.tables.MobModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActorMapper {
    @Mapping(source = "actorId", target = "id")
    MobModel toMobModel(ActorDTO actorDTO);

    @Mapping(source = "id", target = "actorId")
    ActorDTO toActorDTO(MobModel mobModel);
    @Mapping(source = "id", target = "actorId")
    List<ActorDTO> toActorDTO(List<MobModel> mobModelList);
}
