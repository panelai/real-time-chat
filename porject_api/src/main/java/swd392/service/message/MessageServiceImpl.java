package swd392.service.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swd392.model.dto.State;
import swd392.model.dto.message.MessageDTO;
import swd392.model.entity.auth.Account;
import swd392.model.entity.conversation.Conversation;
import swd392.model.entity.message.Message;
import swd392.repository.auth.AccountRepository;
import swd392.repository.conversation.ConversationRepository;
import swd392.repository.message.MessageRepository;
import swd392.security.SecurityUtil;
import swd392.service.notification.MessageChangeNotifier;
import swd392.service.redis.RedisService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageChangeNotifier messageChangeNotifier;

    @Autowired
    private RedisService redisService;

    @Override
    public MessageDTO toMessageDTO(Message message) {
        MessageDTO messageDTO = new MessageDTO();

        messageDTO.setId(message.getId());
        messageDTO.setMessage(message.getMessage());

        messageDTO.setSenderId(message.getSender().getId());
        messageDTO.setSenderName(message.getSender().getUsername());

        messageDTO.setConversationId(message.getConversation().getId());
        messageDTO.setCreatedAt(message.getCreatedAt());
        return messageDTO;
    }

    @Override
    public State<Message, String> send(MessageDTO messageDTO) {
        State<Message, String> creationState;

        Optional<Account> connectedUser = accountRepository.findById(SecurityUtil.getCurrentUserLogin());
        Optional<Conversation> conversation = conversationRepository.findById(messageDTO.getConversationId());
        if(connectedUser.isPresent() && conversation.isPresent()) {
            creationState = create(messageDTO, conversation.get(), connectedUser.get());
        } else {
            creationState = State.<Message, String>builder()
                    .forError(String.format("Error retrieving user information inside the DB : %s", SecurityUtil.getCurrentUsernameLogin()));
        }
        return creationState;
    }

    public State<Message, String> create(MessageDTO messageDTO, Conversation conversation, Account sender) {
        String uid = UUID.randomUUID().toString();
        LocalDateTime createdAt = LocalDateTime.now();
        Message message = new Message();

        message.setId(uid);
        message.setMessage(messageDTO.getMessage());
        message.setSender(sender);
        message.setConversation(conversation);
        message.setCreatedAt(createdAt);

        messageDTO.setId(uid);
        messageDTO.setSenderId(sender.getId());
        messageDTO.setSenderName(sender.getUsername());
        messageDTO.setCreatedAt(createdAt);

        String messageKey = "conversation_" + conversation.getId();
        redisService.addMesage(messageKey, messageDTO);

        messageRepository.save(message);

        if(conversation.getMessages() == null) {
            conversation.setMessages(new ArrayList<String>());
        }
        conversation.getMessages().add(message.getId());
        conversationRepository.save(conversation);

        messageChangeNotifier.send(messageDTO, conversation.getMemberIds());
        return State.<Message, String>builder().forSuccess(message);
    }
}
