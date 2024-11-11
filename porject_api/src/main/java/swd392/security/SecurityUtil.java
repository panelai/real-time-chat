package swd392.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Objects;
import java.util.Optional;

public final class SecurityUtil {
    public static Optional<String> getCurrentUserLoginOptional() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return Objects.nonNull(authentication)? Optional.of(((Jwt) authentication.getCredentials()).getClaims().get("guid").toString()) :
                Optional.empty();
    }

    public static String getCurrentUserLogin() {
        return getCurrentUserLoginOptional().orElseThrow();
    }

    public static Optional<String> getCurrentUsernameLoginOptional() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return Objects.nonNull(authentication) ?
                Optional.of(authentication.getName()) :
                Optional.empty();
    }

    public static String getCurrentUsernameLogin() {
        return getCurrentUsernameLoginOptional().orElseThrow();
    }
}
