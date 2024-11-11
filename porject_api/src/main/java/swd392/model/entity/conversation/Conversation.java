package swd392.model.entity.conversation;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import swd392.enums.ConversationType;
import swd392.model.entity.auth.Account;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "Conversation")  // Use @Document for MongoDB collections
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    @Id  // MongoDB uses @Id for the primary key
    private String id;  // MongoDB ids are typically of type String

    private String title;

    private ConversationType type;

    @Field("group_admin")
    private String groupAdmin;

    private List<String> messages;

    private List<String> memberIds;

    private List<Account> members;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;
}
