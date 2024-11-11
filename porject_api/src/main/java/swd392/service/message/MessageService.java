package swd392.service.message;

import swd392.model.dto.State;
import swd392.model.dto.message.MessageDTO;
import swd392.model.entity.message.Message;


public interface MessageService {
    State<Message, String> send(MessageDTO messageDTO);

    MessageDTO toMessageDTO(Message message);
}
