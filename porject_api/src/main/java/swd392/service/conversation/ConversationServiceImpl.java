package swd392.service.conversation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import swd392.enums.ConversationType;
import swd392.model.dto.State;
import swd392.model.dto.account.MemberDTO;
import swd392.model.dto.conversation.ConversationDTO;
import swd392.model.dto.conversation.ConversationToCreateDTO;
import swd392.model.dto.message.MessageDTO;
import swd392.model.entity.auth.Account;
import swd392.model.entity.conversation.Conversation;
import swd392.repository.conversation.ConversationRepository;
import swd392.repository.message.MessageRepository;
import swd392.security.SecurityUtil;
import swd392.service.auth.AuthService;
import swd392.service.message.MessageService;
import swd392.service.redis.RedisService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private AuthService authService;
    @Autowired
    private MessageService messageService;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RedisService redisService;

    private final static int SIZE = 10;
    @Override
    public List<ConversationDTO> getAllByConnectedUser() {
        String guid = SecurityUtil.getCurrentUserLogin();
        String key = "conversations_" + guid;

        List<ConversationDTO> conversationDTOList = new ArrayList<>();
        if(redisService.checkExists(key)) {
            conversationDTOList = getFromRedis(key);
            if(conversationDTOList.size() >= 10) return conversationDTOList;
        }
        //Pageable pageable = PageRequest.of(0, SIZE - conversationDTOList.size());
        List<Conversation> conversationList = findByMemberIdsContaining(guid, conversationDTOList.size(), SIZE - conversationDTOList.size()).stream()
                .peek(conversation -> {
                    List<String> limitedMessageIds = new ArrayList<>(Optional.ofNullable(conversation.getMessages())
                            .orElse(Collections.emptyList()));
                    conversation.setMessages(limitedMessageIds);
                }).collect(Collectors.toList());
        if(!conversationList.isEmpty()) {
            List<String> allMessageIds = getAllMessageIds(conversationList);
            List<MessageDTO> messageDTOList = messageRepository.findByIdIn(allMessageIds)
                    .stream().map(messageService::toMessageDTO).toList();
            List<ConversationDTO> _conversationDTOList = conversationList.stream().map(this::toConversationDTO).toList();
            conversationDTOList.addAll(saveToRedis(key, _conversationDTOList, messageDTOList));
        }

        return conversationDTOList;
    }

    @Override
    public ConversationDTO getOneByConversationId(String id) {
        String guid = SecurityUtil.getCurrentUserLogin();

        Optional<Conversation> conversation = conversationRepository.findByIdAndMemberIdsContaining(id, guid)
                .map(c -> {
                    List<String> limitedMessageIds = c.getMessages()
                            .stream()
                            .skip(Math.max(0, c.getMessages().size() - 20))
                            .collect(Collectors.toList());
                    c.setMessages(limitedMessageIds);
                    return c;
                });
        if (conversation.isPresent()) {
            List<MessageDTO> messageList = messageRepository.findByIdIn(conversation.get().getMessages())
                    .stream().map(messageService::toMessageDTO).toList();
            //return toConversationDTO(conversation.get(), messageList);
        }
        return null;
    }

    @Override
    public State<ConversationDTO, String> create(ConversationToCreateDTO conversationToCreateDTO) {
        String guid = SecurityUtil.getCurrentUserLogin();

        conversationToCreateDTO.getMemberIds().add(guid);
        List<Account> members = authService.findByIdIn(conversationToCreateDTO.getMemberIds());

        State<ConversationDTO, String> stateResult;
        if (!members.isEmpty()
                && members.size() == conversationToCreateDTO.getMemberIds().size()
        ) {
            List<String> memberIds = members.stream().map(Account::getId).collect(Collectors.toList());

            Optional<Conversation> conversation = conversationRepository.findByExactMemberIds(memberIds);
            if (conversation.isEmpty()) {
                Conversation newConversation = toConversation(conversationToCreateDTO, memberIds, members);
                conversationRepository.save(newConversation);

                ConversationDTO conversationDTO = toConversationDTO(newConversation);

                String key = "conversations_" + guid;
                saveToRedis(key, Collections.singletonList(conversationDTO), null);

                stateResult = State.<ConversationDTO, String>builder().forSuccess(conversationDTO);

                return stateResult;
            }
        }
        stateResult = State.<ConversationDTO, String>builder().forError("This conversation already exists");

        return stateResult;
    }

    public List<ConversationDTO> getFromRedis(String key) {
        List<ConversationDTO> conversationDTOList = redisService.getConversations(key);
        for(ConversationDTO conversationDTO : conversationDTOList) {
            String messagesKey = "conversation_" + conversationDTO.getId();

            List<MessageDTO> messageDTOList = redisService.getMessages(messagesKey);
            conversationDTO.setMessages(messageDTOList);
        }
        return conversationDTOList;
    }

    public List<ConversationDTO> saveToRedis(String key, List<ConversationDTO> conversationDTOList, List<MessageDTO> messageDTOList) {
        if(messageDTOList != null && !messageDTOList.isEmpty()) {
            for(ConversationDTO conversationDTO : conversationDTOList) {
                List<MessageDTO> messages = messageDTOList.stream()
                        .filter(message -> message.getConversationId().equals(conversationDTO.getId()))
                        .toList();
                if(!messages.isEmpty() && !redisService.checkExists(key)) {
                    String messagesKey = "conversation_" + conversationDTO.getId();
                    redisService.saveMessages(messagesKey, messages);
                }
                conversationDTO.setMessages(messages);
            }
        }
        redisService.saveConversations(key, conversationDTOList);
        return conversationDTOList;
    }

    public List<Conversation> findByMemberIdsContaining(String memberId, int skip, int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("memberIds").in(memberId));
        query.with(Sort.by(Sort.Direction.DESC, "updatedAt"));  // Có thể sắp xếp theo một field
        query.skip(skip);  // Bỏ qua n bản ghi đầu tiên
        query.limit(limit);  // Giới hạn số lượng bản ghi trả về

        return mongoTemplate.find(query, Conversation.class);
    }

    @Override
    public ConversationDTO toConversationDTO(Conversation conversation) {
        ConversationDTO dto = new ConversationDTO();

        dto.setId(conversation.getId());
        dto.setTitle(conversation.getTitle());
        dto.setType(conversation.getType());
        dto.setGroupAdmin(conversation.getGroupAdmin());

        if(conversation.getMessages() != null) {
            dto.setMemberIds(conversation.getMessages());
        }
        else {
            dto.setMemberIds(new ArrayList<>());
        }

        dto.setMemberIds(conversation.getMemberIds());
        dto.setMembers(conversation.getMembers().stream().map(a -> new MemberDTO(
                a.getId(),
                a.getUsername()
        )).collect(Collectors.toList()));

        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setUpdatedAt(conversation.getUpdatedAt());
        return dto;
    }

    @Override
    public Conversation toConversation(ConversationToCreateDTO dto, List<String> memberIds, List<Account> members) {
        Conversation conversation = new Conversation();

        conversation.setId(UUID.randomUUID().toString());
        conversation.setTitle(dto.getTitle());
        conversation.setType(dto.getType());

        if(dto.getType().equals(ConversationType.GROUP)) {
            conversation.setGroupAdmin(SecurityUtil.getCurrentUserLogin());
        }

        conversation.setMemberIds(memberIds);
        conversation.setMembers(members);

        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());

        return conversation;
    }

    @Override
    public List<String> getAllMessageIds(List<Conversation> conversations) {
        Set<String> allMessageIds = new HashSet<>();

        // Lặp qua từng Conversation và thêm messageIds vào Set
        for (Conversation conversation : conversations) {
            List<String> messageIds = conversation.getMessages();
            if (messageIds != null) {
                allMessageIds.addAll(messageIds);
            }
        }

        return allMessageIds.stream().toList(); // Trả về Set chứa các ID không trùng lặp
    }
}
