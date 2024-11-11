package swd392.service.conversation;

import org.springframework.data.domain.Pageable;
import swd392.model.dto.State;
import swd392.model.dto.conversation.ConversationDTO;
import swd392.model.dto.conversation.ConversationToCreateDTO;
import swd392.model.dto.message.MessageDTO;
import swd392.model.entity.auth.Account;
import swd392.model.entity.conversation.Conversation;

import java.util.List;

public interface ConversationService {
    List<ConversationDTO> getAllByConnectedUser();

    ConversationDTO getOneByConversationId(String id);

    State<ConversationDTO, String> create(ConversationToCreateDTO conversationToCreateDTO);

    List<ConversationDTO> getFromRedis(String key);

    List<ConversationDTO> saveToRedis(String key, List<ConversationDTO> conversationDTOList, List<MessageDTO> messageDTOList);

    ConversationDTO toConversationDTO(Conversation conversation);

    Conversation toConversation(ConversationToCreateDTO conversationToCreateDTO, List<String> memberIds, List<Account> members);

    List<String> getAllMessageIds(List<Conversation> conversations);
}
