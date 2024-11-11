package swd392.service.auth;

import swd392.model.dto.auth.LoginDTO;
import swd392.model.dto.auth.RegisterDTO;
import swd392.model.entity.auth.Account;

import java.util.List;

public interface AuthService {
    Account login(LoginDTO loginDTO);

    void register(RegisterDTO registerDTO);

    List<Account> findByIdIn(List<String> ids);
}
