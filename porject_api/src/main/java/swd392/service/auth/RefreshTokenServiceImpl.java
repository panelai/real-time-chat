package swd392.service.auth;

import org.springframework.stereotype.Service;
import swd392.model.entity.auth.RefreshToken;
import swd392.repository.auth.RefreshTokenRepository;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.repository = refreshTokenRepository;
    }

    @Override
    public RefreshToken create(RefreshToken refreshToken) {
        return repository.save(refreshToken);
    }

    @Override
    public RefreshToken update(RefreshToken refreshToken) {
        return repository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findValidToken(String refreshToken) {
        return repository.findByRefreshTokenAndExpiredTimeAfter(refreshToken, Instant.now());
    }
}
