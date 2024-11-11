package swd392.resource.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swd392.model.dto.auth.LoginDTO;
import swd392.model.dto.auth.LoginResponseDTO;
import swd392.model.dto.auth.RegisterDTO;
import swd392.model.entity.auth.Account;
import swd392.model.entity.auth.RefreshToken;
import swd392.security.TokenProvider;
import swd392.service.auth.AuthService;
import swd392.service.auth.RefreshTokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationResource {
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

    public AuthenticationResource(TokenProvider tokenProvider, RefreshTokenService refreshTokenService, AuthService authService) {
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody @Valid LoginDTO loginDTO) {
        Account account = authService.login(loginDTO);

        if (account != null) {
            String guid = account.getId();
            String username = account.getUsername();

            RefreshToken refreshToken = tokenProvider.generateRefreshToken(username);
            refreshTokenService.create(refreshToken);

            LoginResponseDTO loginResponseDto = LoginResponseDTO.builder()
                    .accessToken(tokenProvider.generateAccessToken(username, guid))
                    .refreshToken(refreshToken.getRefreshToken())
                    .guid(guid)
                    .username(username)
                    .email(account.getEmail())
                    .build();
            return ResponseEntity.ok(loginResponseDto);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> Register(@RequestBody @Valid RegisterDTO registerDTO) {
        authService.register(registerDTO);

        return ResponseEntity.ok().build();
    }
}
