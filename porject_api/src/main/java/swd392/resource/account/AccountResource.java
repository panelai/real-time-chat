package swd392.resource.account;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import swd392.model.dto.account.AccountInfo;
import swd392.service.account.AccountService;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountResource {
    private final AccountService accountService;

    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<AccountInfo>> search(@RequestParam String query, Pageable pageable) {
        List<AccountInfo> searchResults = accountService.search(pageable, query);
        return ResponseEntity.ok(searchResults);
    }
}
