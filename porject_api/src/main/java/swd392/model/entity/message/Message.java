package swd392.model.entity.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import swd392.model.entity.auth.Account;
import swd392.model.entity.conversation.Conversation;

import java.time.LocalDateTime;

@Document("message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    private String id;

    private String message;

    private Account sender;

    private Conversation conversation;

    @Field("created_at")
    private LocalDateTime createdAt;
}
