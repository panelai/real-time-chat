package swd392.model.entity.auth;

import lombok.*;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "refresh_token")  // Use @Document for MongoDB collections
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id  // MongoDB uses @Id for the primary key
    private String id;  // MongoDB ids are typically of type String

    private String account;

    @Field("refresh_token")  // Use @Field to map fields to custom names if needed
    private String refreshToken;

    @Field("expired_time")
    private Instant expiredTime;
}
