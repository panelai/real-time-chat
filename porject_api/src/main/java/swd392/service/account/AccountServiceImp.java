package swd392.service.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swd392.model.dto.account.AccountInfo;
import swd392.model.entity.auth.Account;
import swd392.repository.auth.AccountRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImp implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<AccountInfo> search(Pageable pageable, String query) {
        return accountRepository.findByUsernameOrEmailContaining(query, pageable).stream()
                .map(this::toAccountInfo)
                .collect(Collectors.toList());
    }

    public AccountInfo toAccountInfo(Account account) {
        AccountInfo accountInfo = new AccountInfo();

        accountInfo.setId(account.getId());
        accountInfo.setUsername(account.getUsername());
        accountInfo.setEmail(account.getEmail());

        return accountInfo;
    }
}
