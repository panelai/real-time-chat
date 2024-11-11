package swd392.model.entity.auth;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import swd392.enums.UserRole;

import java.time.Instant;

@Document(collection = "Account")  // Use @Document for MongoDB collections
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id  // MongoDB uses @Id for the primary key
    private String id;  // MongoDB ids are typically of type String

    private String username;

    private String password;

    private String email;

    private String phone;

    private String introduction;

    private UserRole role;

    @Field("is_Active")
    private boolean isActived;
}
