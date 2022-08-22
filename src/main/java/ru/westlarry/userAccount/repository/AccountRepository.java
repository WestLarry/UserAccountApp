package ru.westlarry.userAccount.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.westlarry.userAccount.entity.Account;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("select id from Account")
    List<Long> findAllIds();

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    Optional<Account> findById(Long id); //на получение аккаунта происходит пессимистическая блокировка
}
