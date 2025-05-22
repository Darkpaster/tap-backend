package com.human.tapMMO.mapper;

import com.human.tapMMO.dto.websocket.ActorDTO;
import com.human.tapMMO.model.tables.Character;
import com.human.tapMMO.model.tables.CharacterStats;
import com.human.tapMMO.model.tables.MobModel;
import com.human.tapMMO.runtime.game.actors.mob.Mob;
import com.human.tapMMO.runtime.game.actors.player.Player;
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
    ActorDTO toActorDTOFromPlayer(Player player);

    @Mapping(source = "id", target = "actorId")
    List<ActorDTO> toActorDTOFromPlayer(List<Player> playerList);

    @Mapping(source = "id", target = "actorId")
    ActorDTO toActorDTOFromModel(MobModel mobModel);

    @Mapping(source = "id", target = "actorId")
    List<ActorDTO> toActorDTOFromModel(List<MobModel> mobModelList);

    @Mapping(source = "id", target = "actorId")
    ActorDTO toActorDTOFromMob(Mob mob);

    @Mapping(source = "id", target = "actorId")
    List<ActorDTO> toActorDTOFromMob(List<Mob> mobList);

    @Mapping(source = "character.id", target = "id")
    Player toPlayer(Character character, CharacterStats characterStats);

    Character toCharacter(Player player);
    @Mapping(source = "id", target = "characterId")
    CharacterStats toCharacterStats(Player player);
}
