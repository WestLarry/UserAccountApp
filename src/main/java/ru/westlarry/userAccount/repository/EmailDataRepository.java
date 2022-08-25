package ru.westlarry.userAccount.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.westlarry.userAccount.entity.EmailData;

public interface EmailDataRepository extends JpaRepository<EmailData, Long> {
    boolean existsByEmail(String email);

    //List<EmailData> findByEmailAndUserId(String email, Long userId);
}
