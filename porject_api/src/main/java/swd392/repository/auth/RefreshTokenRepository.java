package swd392.repository.auth;

import org.springframework.data.repository.CrudRepository;
import swd392.model.entity.auth.RefreshToken;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshTokenAndExpiredTimeAfter(String refreshToken, Instant now);
}
