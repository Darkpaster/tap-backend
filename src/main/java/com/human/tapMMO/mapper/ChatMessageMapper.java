package com.human.tapMMO.mapper;

import com.human.tapMMO.dto.websocket.ChatMessage;
import com.human.tapMMO.model.tables.ChatMessageModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatMessageMapper {
    ChatMessage toChatMessage(ChatMessageModel chatMessageModel);
    ChatMessageModel toChatMessageModel(ChatMessage chatMessage);
}
