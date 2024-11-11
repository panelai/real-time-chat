package swd392.repository.auth;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import swd392.model.entity.auth.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, String> {
    Optional<Account> findByEmailAndPassword(String username, String password);

    Optional<List<Account>> findByIdIn(List<String> ids);

    @Query("{ '$or': [ { 'username': { '$regex': ?0, '$options': 'i' } }, { 'email': { '$regex': ?0, '$options': 'i' } } ] }")
    List<Account> findByUsernameOrEmailContaining(String query, Pageable pageable);
}
