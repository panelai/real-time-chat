package swd392.repository.message;

import org.springframework.data.mongodb.repository.MongoRepository;
import swd392.model.entity.message.Message;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByIdIn(List<String> messageIds);
}
