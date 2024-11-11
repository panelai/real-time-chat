package swd392.repository.conversation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import swd392.model.entity.conversation.Conversation;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends MongoRepository<Conversation, String> {
    @Query(value = "{ 'memberIds': ?0 }", sort = "{ 'updatedAt': 1 }")
    List<Conversation> findByMemberIdsContaining(String userId, Pageable pageable);

    Optional<Conversation> findByIdAndMemberIdsContaining(String conversationId, String userId);

//    @Query("{ 'memberIds': { '$all': ?0 }, 'memberIds': { '$size': ?#{[0].size()} } }")
//    Optional<Conversation> findByExactMemberIds(List<String> memberIds);

    @Query("{ '$and': [ { 'memberIds': { '$all': ?0 } }, { 'memberIds': { '$size': ?#{[0].size()} } } ] }")
    Optional<Conversation> findByExactMemberIds(List<String> memberIds);
}
