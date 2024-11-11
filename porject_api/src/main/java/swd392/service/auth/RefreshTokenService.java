package swd392.service.auth;

import swd392.model.entity.auth.RefreshToken;

import java.util.Optional;


public interface RefreshTokenService {
    RefreshToken create(RefreshToken refreshToken);
    RefreshToken update(RefreshToken refreshToken);

    Optional<RefreshToken> findValidToken(String refreshToken);
}
