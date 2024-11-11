package swd392.service.account;

import org.springframework.data.domain.Pageable;
import swd392.model.dto.account.AccountInfo;

import java.util.List;

public interface AccountService {
    List<AccountInfo> search(Pageable pageable, String query);
}
