package swd392.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swd392.enums.UserRole;
import swd392.model.dto.auth.LoginDTO;
import swd392.model.dto.auth.RegisterDTO;
import swd392.model.entity.auth.Account;
import swd392.repository.auth.AccountRepository;
import swd392.security.EncryptionUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account login(LoginDTO loginDTO) {
        try {
            String email = loginDTO.getEmail();
            String password = EncryptionUtil.encrypt(loginDTO.getPassword());

            return accountRepository.findByEmailAndPassword(email, password)
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        Account account = new Account();
        try
        {
            account.setId(UUID.randomUUID().toString());
            account.setEmail(registerDTO.getEmail());
            account.setUsername(registerDTO.getUsername());
            account.setPassword(EncryptionUtil.encrypt(registerDTO.getPassword()));
            account.setRole(UserRole.USER);
            account.setActived(false);

            accountRepository.save(account);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Account> findByIdIn(List<String> ids) {
        Optional<List<Account>> accountList = accountRepository.findByIdIn(ids);
        return accountList.orElse(null);
    }
}
