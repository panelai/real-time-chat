package swd392.service.redis;

import org.hibernate.validator.internal.util.logging.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import swd392.model.dto.conversation.ConversationDTO;
import swd392.model.dto.message.MessageDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate redisTemplate;

    public boolean checkExists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public List<ConversationDTO> getConversations(String key) {
        ListOperations<String, ConversationDTO> listOps = redisTemplate.opsForList();
        return listOps.range(key, -10, -1);
    }

    public void saveConversations(String key, List<ConversationDTO> conversations) {
        ListOperations<String, ConversationDTO> listOps = redisTemplate.opsForList();
        listOps.rightPushAll(key, conversations);
    }

    public void addConversation(String key, ConversationDTO conversation) {
        ListOperations<String, ConversationDTO> listOps = redisTemplate.opsForList();
        listOps.rightPush(key, conversation); // Thêm conversation vào cuối danh sách
    }

    public List<MessageDTO> getMessages(String key) {
        ListOperations<String, MessageDTO> listOps = redisTemplate.opsForList();
        return listOps.range(key, -10, -1);
    }

    public void saveMessages(String key, List<MessageDTO> messages) {
        ListOperations<String, MessageDTO> listOps = redisTemplate.opsForList();
        listOps.rightPushAll(key, messages);
    }

    public void addMesage(String key, MessageDTO message) {
        ListOperations<String, MessageDTO> listOps = redisTemplate.opsForList();
        listOps.rightPush(key, message); // Thêm conversation vào cuối danh sách
    }
}
