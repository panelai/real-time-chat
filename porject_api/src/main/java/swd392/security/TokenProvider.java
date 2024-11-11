package swd392.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import swd392.model.entity.auth.RefreshToken;
import swd392.utils.RandomUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
public class TokenProvider {
    //@Value("${app.security.access-token-expired-in-second}")
    private Integer accessTokenExpiredInSecond = 3600;

    //@Value("${app.security.refresh-token-expired-in-second}")
    private Integer refreshTokenExpiredInSecond = 86400;

    private final JwtEncoder jwtEncoder;

    public TokenProvider(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateAccessToken(Authentication authentication) {
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return generateAccessToken(authentication.getName(), roles);
    }

    public String generateAccessToken(String account, String uid) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiredInSecond, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(account)
                .claim("guid", uid)
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public RefreshToken generateRefreshToken(String account) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiredInSecond, ChronoUnit.SECONDS);
        String token = RandomUtils.generateRandomAlphanumericString(200);

        return RefreshToken.builder()
                .account(account)
                .refreshToken(token)
                .expiredTime(validity)
                .build();
    }
}
