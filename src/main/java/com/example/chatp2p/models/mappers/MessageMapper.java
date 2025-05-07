package com.example.chatp2p.models.mappers;

import com.example.chatp2p.models.dto.Message;
import com.example.chatp2p.models.entitities.MessageDataAccessObject;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    MessageDataAccessObject messageToMessageDataAccessObject(Message message);

    Message messageDataAccessObjectToMessage(MessageDataAccessObject message);

    List<Message> messageDataAccessObjectListToMessageList(List<MessageDataAccessObject> message);
}
